package com.example.green.domain.pointshop.item.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.item.dto.response.PointItemAdminResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemClientResponse;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointItemQueryService {

	private final PointItemRepository pointItemRepository;
	private final PointItemService pointItemService;

	//id 기준으로 Item 조회
	public PointItem getPointItem(Long id) {

		return pointItemRepository.findById(id)
			.orElseThrow(() -> new PointItemException(PointItemExceptionMessage.NOT_FOUND_ITEM));
	}

	public void validateUniqueCodeForUpdate(ItemCode code, Long id) {
		if (pointItemRepository.existsByItemCodeAndIdNot(code, id)) {
			throw new PointItemException(PointItemExceptionMessage.DUPLICATE_POINT_ITEM_CODE);
		}
	}

	public PointItemAdminResponse getPointItemAdminResponse(Long id) {
		PointItem pointItem = getPointItem(id);

		return new PointItemAdminResponse(
			pointItem.getItemBasicInfo().getItemName(),
			pointItem.getItemBasicInfo().getDescription(),
			pointItem.getItemMedia().getItemThumbNailUrl(),
			pointItem.getItemPrice().getItemPrice()
		);
	}

	public PointItemClientResponse getPointItemInfo(Long memberId, Long id) {

		PointItem pointItem = getPointItem(id);
		List<BigDecimal> points = pointItemService.userPointsCalculate(memberId, id);
		BigDecimal enablePoint = points.get(0);
		BigDecimal decreasePoint = points.get(1);
		BigDecimal remainPoint = points.get(2);

		return new PointItemClientResponse(
			pointItem.getItemBasicInfo().getItemName(),
			pointItem.getItemBasicInfo().getDescription(),
			pointItem.getItemMedia().getItemThumbNailUrl(),
			pointItem.getItemPrice().getItemPrice(),
			enablePoint,
			decreasePoint,
			remainPoint
		);
	}

}
