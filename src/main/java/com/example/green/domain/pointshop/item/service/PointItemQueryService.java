package com.example.green.domain.pointshop.item.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.item.dto.response.PointItemAdminResponse;
import com.example.green.domain.pointshop.item.dto.response.UserPointCalculation;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.infra.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointItemQueryService {

	private final PointItemRepository pointItemRepository;
	private final PointClient pointClient;

	//id 기준으로 Item 조회 (admin)
	public PointItem getPointItem(Long id) {

		return pointItemRepository.findById(id)
			.orElseThrow(() -> new PointItemException(PointItemExceptionMessage.NOT_FOUND_ITEM));
	}

	public UserPointCalculation userPointsCalculate(Long memberId, Long pointItemId) {

		BigDecimal enablePoint = pointClient.getTotalPoints(memberId);

		PointItem pointItem = getPointItem(pointItemId);

		BigDecimal decreasePoint = pointItem.getItemPrice().getItemPrice(); //차감 포인트

		if (enablePoint.compareTo(decreasePoint) < 0) {
			return new UserPointCalculation(enablePoint, decreasePoint, BigDecimal.ZERO);
		}

		BigDecimal remainPoint = enablePoint.subtract(decreasePoint);

		return new UserPointCalculation(enablePoint, decreasePoint, remainPoint);
	}

	public void validateUniqueCodeForUpdate(ItemCode code, Long id) {
		if (pointItemRepository.existsByItemCodeAndIdNot(code, id)) {
			throw new PointItemException(PointItemExceptionMessage.DUPLICATE_POINT_ITEM_CODE);
		}
	}

	public PointItemAdminResponse getPointItemAdminResponse(Long id) {
		PointItem pointItem = getPointItem(id);

		return PointItemAdminResponse.from(pointItem);
	}

}
