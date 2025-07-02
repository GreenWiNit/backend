package com.example.green.domain.pointshop.service;

import org.springframework.stereotype.Service;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;
import com.example.green.domain.pointshop.repository.PointProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointProductDomainService {

	private final PointProductRepository pointProductRepository;

	public PointProduct getPointProduct(Long id) {
		return pointProductRepository.findById(id)
			.orElseThrow(() -> new PointProductException(PointProductExceptionMessage.NOT_FOUND_POINT_PRODUCT));
	}

	public void validateUniqueCodeForUpdate(String code, Long id) {
		if (pointProductRepository.existsByBasicInfoCodeAndIdNot(code, id)) {
			throw new PointProductException(PointProductExceptionMessage.DUPLICATE_POINT_PRODUCT_CODE);
		}
	}
}
