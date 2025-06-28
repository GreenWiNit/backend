package com.example.green.domain.pointshop.entity.pointproduct.vo;

import static com.example.green.global.utils.EntityValidator.*;

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
		validateNullData(price, "상품 가격이 null 값 입니다.");
		if (price < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_PRICE);
		}
		this.price = price;
	}
}