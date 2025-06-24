package com.example.green.domain.pointshop.entity.vo;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

public record Stock(Integer stock) {

	public Stock {
		if (stock == null || stock < 1) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_STOCK);
		}
	}
}