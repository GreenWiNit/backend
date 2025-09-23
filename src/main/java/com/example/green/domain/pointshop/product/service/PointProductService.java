package com.example.green.domain.pointshop.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.domain.pointshop.product.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.product.service.command.PointProductUpdateCommand;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointProductService {

	private final PointProductQueryService pointProductQueryService;
	private final PointProductRepository pointProductRepository;
	private final FileClient fileClient;

	public Long create(PointProductCreateCommand command) {
		validateProductCode(command.code());
		PointProduct pointProduct = PointProduct.create(
			command.code(),
			command.basicInfo(),
			command.media(),
			command.price(),
			command.stock()
		);

		PointProduct saved = pointProductRepository.save(pointProduct);
		return saved.getId();
	}

	private void validateProductCode(Code code) {
		if (pointProductRepository.existsByCode(code)) {
			throw new PointProductException(PointProductExceptionMessage.EXISTS_PRODUCT_CODE);
		}
	}

	public void update(PointProductUpdateCommand command, Long pointProductId) {
		pointProductQueryService.validateUniqueCodeForUpdate(command.code(), pointProductId);

		PointProduct pointProduct = pointProductQueryService.getPointProduct(pointProductId);
		pointProduct.updateCode(command.code());
		pointProduct.updateBasicInfo(command.basicInfo());
		pointProduct.updatePrice(command.price());
		pointProduct.updateStock(command.stock());

		processSideEffect(command, pointProduct);
	}

	private void processSideEffect(PointProductUpdateCommand command, PointProduct pointProduct) {
		if (pointProduct.isNewImage(command.media())) {
			fileClient.unUseImage(pointProduct.getThumbnailUrl());
			pointProduct.updateMedia(command.media());
			fileClient.confirmUsingImage(pointProduct.getThumbnailUrl());
		}
	}

	public void delete(Long pointProductId) {
		pointProductQueryService.getPointProduct(pointProductId).markDeleted();
	}

	public void showDisplay(Long pointProductId) {
		pointProductQueryService.getPointProduct(pointProductId).showDisplay();
	}

	public void hideDisplay(Long pointProductId) {
		pointProductQueryService.getPointProduct(pointProductId).hideDisplay();
	}

	public ItemSnapshot getItemSnapshot(Long pointProductId) {
		PointProduct pointProduct = pointProductQueryService.getPointProduct(pointProductId);
		return new ItemSnapshot(
			pointProduct.getId(),
			pointProduct.getBasicInfo().getName(),
			pointProduct.getCode().getCode(),
			pointProduct.getPrice().getPrice()
		);
	}

	public void decreaseSingleItemStock(Long pointProductId, int amount) {
		PointProduct pointProduct = pointProductQueryService.getPointProductWithPessimisticLock(pointProductId);
		pointProduct.decreaseStock(amount);
	}
}
