package com.example.green.domain.pointshop.entity.vo;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

public record Price(Integer price) {

	public Price {
		if (price == null || price < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_PRICE);
		}
	}
}