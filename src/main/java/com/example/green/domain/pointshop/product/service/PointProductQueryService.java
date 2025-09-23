package com.example.green.domain.pointshop.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointProductQueryService {

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

	public PointProduct getPointProductWithPessimisticLock(Long id) {
		return pointProductRepository.findByIdWithPessimisticLock(id)
			.orElseThrow(() -> new PointProductException(PointProductExceptionMessage.NOT_FOUND_POINT_PRODUCT));
	}
}
