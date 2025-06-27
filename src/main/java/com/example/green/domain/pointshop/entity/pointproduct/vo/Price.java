package com.example.green.domain.pointshop.entity.pointproduct.vo;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Price {

	@Column(nullable = false)
	private Integer price;

	public Price(Integer price) {
		if (price == null || price < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_PRICE);
		}
		this.price = price;
	}
}