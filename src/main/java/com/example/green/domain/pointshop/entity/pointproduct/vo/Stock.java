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
public class Stock {

	@Column(nullable = false)
	private Integer stock;

	public Stock(Integer stock) {
		if (stock == null || stock < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_STOCK);
		}
		this.stock = stock;
	}

	public Stock decrease(int amount) {
		if (this.stock < amount) {
			throw new PointProductException(PointProductExceptionMessage.OUT_OF_PRODUCT_STOCK);
		}
		return new Stock(this.stock - amount);
	}

	public boolean isSoldOut() {
		return this.stock == 0;
	}
}