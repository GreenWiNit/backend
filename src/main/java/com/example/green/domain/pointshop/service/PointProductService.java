package com.example.green.domain.pointshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.repository.PointProductRepository;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointProductService {

	private final PointProductRepository pointProductRepository;

	public Long create(PointProductCreateCommand command) {
		PointProduct pointProduct = PointProduct.create(
			command.basicInfo(),
			command.media(),
			command.price(),
			command.stock()
		);
		PointProduct saved = pointProductRepository.save(pointProduct);
		return saved.getId();
	}
}
