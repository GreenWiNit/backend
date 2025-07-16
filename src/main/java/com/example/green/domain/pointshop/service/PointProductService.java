package com.example.green.domain.pointshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;
import com.example.green.domain.pointshop.repository.PointProductRepository;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.service.command.PointProductUpdateCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointProductService {

	private final PointProductDomainService pointProductDomainService;
	private final PointProductRepository pointProductRepository;
	private final FileManager fileManager;

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
		pointProductDomainService.validateUniqueCodeForUpdate(command.code(), pointProductId);

		PointProduct pointProduct = pointProductDomainService.getPointProduct(pointProductId);
		pointProduct.updateCode(command.code());
		pointProduct.updateBasicInfo(command.basicInfo());
		pointProduct.updatePrice(command.price());
		pointProduct.updateStock(command.stock());

		processSideEffect(command, pointProduct);
	}

	private void processSideEffect(PointProductUpdateCommand command, PointProduct pointProduct) {
		if (pointProduct.isNewImage(command.media())) {
			fileManager.unUseImage(pointProduct.getThumbnailUrl());
			pointProduct.updateMedia(command.media());
			fileManager.confirmUsingImage(pointProduct.getThumbnailUrl());
		}
	}

	public void delete(Long pointProductId) {
		pointProductDomainService.getPointProduct(pointProductId).markDeleted();
	}

	public void showDisplay(Long pointProductId) {
		pointProductDomainService.getPointProduct(pointProductId).showDisplay();
	}

	public void hideDisplay(Long pointProductId) {
		pointProductDomainService.getPointProduct(pointProductId).hideDisplay();
	}

	public ItemSnapshot getItemSnapshot(Long pointProductId) {
		PointProduct pointProduct = pointProductDomainService.getPointProduct(pointProductId);
		return new ItemSnapshot(
			pointProduct.getId(),
			pointProduct.getBasicInfo().getName(),
			pointProduct.getCode().getCode(),
			pointProduct.getPrice().getPrice()
		);
	}

	public void decreaseSingleItemStock(Long pointProductId, int amount) {
		pointProductDomainService.getPointProduct(pointProductId).decreaseStock(amount);
	}
}
