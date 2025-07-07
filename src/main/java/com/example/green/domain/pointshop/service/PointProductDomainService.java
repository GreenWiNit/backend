package com.example.green.domain.pointshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;
import com.example.green.domain.pointshop.repository.PointProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointProductDomainService {

	private final PointProductRepository pointProductRepository;

	public PointProduct getPointProduct(Long id) {
		return pointProductRepository.findById(id)
			.orElseThrow(() -> new PointProductException(PointProductExceptionMessage.NOT_FOUND_POINT_PRODUCT));
	}

	public void validateUniqueCodeForUpdate(Code code, Long id) {
		if (pointProductRepository.existsByCodeAndIdNot(code, id)) {
			throw new PointProductException(PointProductExceptionMessage.DUPLICATE_POINT_PRODUCT_CODE);
		}
	}
}
