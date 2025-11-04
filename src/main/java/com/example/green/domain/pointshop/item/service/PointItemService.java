package com.example.green.domain.pointshop.item.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.item.dto.response.PointItemClientResponse;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
import com.example.green.infra.client.FileClient;
import com.example.green.infra.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointItemService {

	private final PointItemRepository pointItemRepository;
	private final PointItemQueryService pointItemQueryService;
	private final FileClient fileClient;
	private final PointClient pointClient;

	public Long create(PointItemCreateCommand createCommand) {
		validatePointItemCode(createCommand.itemCode());
		PointItem pointItem = PointItem.create(
			createCommand.itemCode(),
			createCommand.info(),
			createCommand.media(),
			createCommand.price()
		);

		PointItem savedPointItem = pointItemRepository.save(pointItem);
		return savedPointItem.getId();
	}

	private void validatePointItemCode(ItemCode itemCode) {
		if (pointItemRepository.existsByItemCode(itemCode)) {
			throw new PointItemException(PointItemExceptionMessage.EXISTS_ITEM_CODE);
		}
	}

	public void updatePointItem(PointItemUpdateCommand command, Long pointItemId) {
		pointItemQueryService.validateUniqueCodeForUpdate(command.itemCode(), pointItemId);

		PointItem pointItem = pointItemQueryService.getPointItem(pointItemId);
		pointItem.updateItemCode(command.itemCode());
		pointItem.updateItemBasicInfo(command.info());
		pointItem.updateItemMedia(command.media());
		pointItem.updateItemPrice(command.price());

		processSideEffect(command, pointItem);
	}

	private void processSideEffect(PointItemUpdateCommand command, PointItem pointItem) {
		if (pointItem.isNewImage(command.media())) {
			String oldThumbnail = pointItem.getThumbnailUrl();
			if (oldThumbnail != null) {
				fileClient.unUseImage(oldThumbnail);
			}
			fileClient.confirmUsingImage(pointItem.getThumbnailUrl());
		}
	}

	public void delete(Long pointItemId) {
		pointItemQueryService.getPointItem(pointItemId).markDeleted();
	}

	public PointItemClientResponse getPointItemInfo(Long memberId, Long id) {

		PointItem pointItem = pointItemQueryService.getPointItem(id);
		List<BigDecimal> points = userPointsCalculate(memberId, id);
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

	public List<BigDecimal> userPointsCalculate(Long memberId, Long pointProduceId) {

		BigDecimal enablePoint = pointClient.getTotalPoints(memberId);

		PointItem pointItem = pointItemQueryService.getPointItem(pointProduceId);

		BigDecimal decreasePoint = pointItem.getItemPrice().getItemPrice(); //차감 포인트

		if (enablePoint.compareTo(decreasePoint) < 0) {
			throw new PointItemException(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM);
		}

		BigDecimal remainPoint = enablePoint.subtract(decreasePoint);

		return List.of(enablePoint, decreasePoint, remainPoint);
	}

	public void showItemDisplay(Long pointItemId) {
		pointItemQueryService.getPointItem(pointItemId).showItemDisplay();
	}

	public void hideItemDisplay(Long pointItemId) {
		pointItemQueryService.getPointItem(pointItemId).hideItemDisplay();
	}

}
