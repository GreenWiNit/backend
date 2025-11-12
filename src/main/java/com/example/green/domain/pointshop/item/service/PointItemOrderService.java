package com.example.green.domain.pointshop.item.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;
import com.example.green.domain.member.repository.MemberRepository;
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
	private final PointClient pointClient;
	private final MemberRepository memberRepository;
	private final TimeUtils timeUtils;

	public OrderPointItemResponse orderPointItem(OrderPointItemCommand command) {

		MemberSnapshot memberSnapshot = command.memberSnapshot();
		PointItemSnapshot itemSnapshot = command.pointItemSnapshot();

		String memberKey = memberSnapshot.getMemberKey();
		Long memberId = memberSnapshot.getMemberId();

		Long itemId = itemSnapshot.getPointItemId();
		String itemName = itemSnapshot.getItemName();
		String itemImgUrl = itemSnapshot.getItemImgUrl();

		existMember(memberKey);
		existItem(itemId);

		BigDecimal itemPrice = itemSnapshot.getItemPrice();
		BigDecimal currentPoint = pointClient.getTotalPoints(memberId);

		if (currentPoint == null || currentPoint.compareTo(itemPrice) < 0) {
			throw new PointItemException(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM);
		}

		boolean alreadyBuy = pointItemOrderRepository.existsByMemberIdAndPointItemId(memberId, itemId);

		if (alreadyBuy) {
			throw new PointItemException(PointItemExceptionMessage.ALREADY_PURCHASED_ITEM);
		}

		BigDecimal remainPoint = currentPoint.subtract(itemPrice);

		pointClient.spendPoints(
			new PointSpendRequest(
				memberId, itemPrice, itemId, itemName + "아이템 구매", timeUtils.now()
			)
		);

		OrderPointItem order = OrderPointItem.builder()
			.memberSnapshot(memberSnapshot)
			.pointItemSnapshot(itemSnapshot)
			.build();

		pointItemOrderRepository.save(order);

		PlantGrowthItem userItem = PlantGrowthItem.create(
			memberId,
			itemName,
			itemImgUrl
		);

		plantGrowthItemRepository.save(userItem);

		return new OrderPointItemResponse(
			memberId,
			itemName,
			itemImgUrl,
			remainPoint
		);

	}

	public void existItem(Long itemId) {
		if (pointItemRepository.findById(itemId).isEmpty()) {
			throw new PointItemException(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM);
		}
	}

	public void existMember(String memberKey) {
		if (!memberRepository.existsByMemberKey(memberKey)) {
			throw new PointItemException(PointItemExceptionMessage.NOT_FOUND_USER);
		}
	}

}
