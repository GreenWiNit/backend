package com.example.green.domain.pointshop.product.entity.vo;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage;

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

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price;

	public Price(BigDecimal price) {
		validateNullData(price, REQUIRED_PRICE);
		if (price.compareTo(BigDecimal.ZERO) < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_PRICE);
		}
		this.price = price.setScale(2, RoundingMode.DOWN);
	}
}
