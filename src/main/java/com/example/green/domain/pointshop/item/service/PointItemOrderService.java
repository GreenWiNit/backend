package com.example.green.domain.pointshop.item.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.pointshop.item.dto.request.OrderPointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.OrderPointItemResponse;
import com.example.green.domain.pointshop.item.entity.OrderPointItem;
import com.example.green.domain.pointshop.item.entity.vo.PointItemSnapshot;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemOrderRepository;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.command.OrderPointItemCommand;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.client.PointClient;
import com.example.green.infra.client.request.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointItemOrderService {

	private final PointItemOrderRepository pointItemOrderRepository;
	private final PointItemRepository pointItemRepository;
	private final PlantGrowthItemRepository plantGrowthItemRepository;
	private final PointItemService pointItemService;
	private final PointClient pointClient;
	private final MemberRepository memberRepository;
	private final TimeUtils timeUtils;

	public OrderPointItemResponse orderPointItem(OrderPointItemCommand command,
		OrderPointItemRequest orderPointItemRequest) {

		MemberSnapshot memberSnapshot = command.memberSnapshot();
		PointItemSnapshot itemSnapshot = command.pointItemSnapshot();
		Integer amount = orderPointItemRequest.amount();

		Long memberId = memberSnapshot.getMemberId();

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new PointItemException(PointItemExceptionMessage.NOT_FOUND_USER));

		Long itemId = itemSnapshot.getPointItemId();
		String itemName = itemSnapshot.getItemName();
		String itemImgUrl = itemSnapshot.getItemImgUrl();

		existItem(itemId);

		BigDecimal itemPrice = itemSnapshot.getItemPrice();
		BigDecimal currentPoint = pointClient.getTotalPoints(memberId);
		BigDecimal totalPoint = itemPrice.multiply(BigDecimal.valueOf(amount));

		if (currentPoint == null || currentPoint.compareTo(totalPoint) < 0) {
			throw new PointItemException(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM);
		}

		pointItemService.decreaseItemStock(itemId, amount);

		BigDecimal remainPoint = currentPoint.subtract(totalPoint);

		pointClient.spendPoints(
			new PointSpendRequest(
				memberId, totalPoint, itemId, itemName + "아이템 구매", timeUtils.now()
			)
		);

		OrderPointItem order = OrderPointItem.builder()
			.memberSnapshot(memberSnapshot)
			.pointItemSnapshot(itemSnapshot)
			.build();

		pointItemOrderRepository.save(order);

		PlantGrowthItem userItem = PlantGrowthItem.create(
			member,
			itemName,
			itemImgUrl
		);

		plantGrowthItemRepository.save(userItem);

		return new OrderPointItemResponse(
			memberId,
			itemName,
			itemImgUrl,
			remainPoint,
			amount
		);

	}

	public void existItem(Long itemId) {
		if (pointItemRepository.findById(itemId).isEmpty()) {
			throw new PointItemException(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM);
		}
	}

}
