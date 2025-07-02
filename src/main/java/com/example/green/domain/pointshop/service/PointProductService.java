package com.example.green.domain.pointshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
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

	private final PointProductRepository pointProductRepository;

	public Long create(PointProductCreateCommand command) {
		validateProductCode(command.basicInfo());
		PointProduct pointProduct = PointProduct.create(
			command.basicInfo(),
			command.media(),
			command.price(),
			command.stock());

		PointProduct saved = pointProductRepository.save(pointProduct);
		return saved.getId();
	}

	private void validateProductCode(BasicInfo basicInfo) {
		if (pointProductRepository.existsByBasicInfoCode(basicInfo.getCode())) {
			throw new PointProductException(PointProductExceptionMessage.EXISTS_PRODUCT_CODE);
		}
	}

	public void update(PointProductUpdateCommand command, Long pointProductId) {
	}
}
