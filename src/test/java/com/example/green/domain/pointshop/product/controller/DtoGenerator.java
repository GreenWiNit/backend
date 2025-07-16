package com.example.green.domain.pointshop.product.controller;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.product.controller.dto.PointProductUpdateDto;

public class DtoGenerator {

	public static PointProductCreateDto getCreateDto() {
		return new PointProductCreateDto(
			"PRD-AA-001",
			"상품1",
			"내용",
			"https://thumbnail.url/image.jpg",
			BigDecimal.valueOf(1000),
			100
		);
	}

	public static PointProductUpdateDto getUpdateDto() {
		return new PointProductUpdateDto(
			"PRD-AA-001",
			"상품1",
			"내용",
			"https://thumbnail.url/image.jpg",
			BigDecimal.valueOf(1000),
			100
		);
	}
}
