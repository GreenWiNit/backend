package com.example.green.domain.pointshop.item.entity.vo;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.pointshop.item.exception.PointItemException;

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
public class ItemStock {

	@Column(nullable = false)
	private Integer stock;

	public ItemStock(Integer stock) {
		validateNullData(stock, REQUIRED_ITEM_STOCK);
		if (stock < 0) {
			throw new PointItemException(INVALID_ITEM_STOCK);
		}
		this.stock = stock;
	}

	public ItemStock decreaseStock(int amount) {
		if (this.stock < amount) {
			throw new PointItemException(OUT_OF_ITEM_STOCK);
		}
		return new ItemStock(this.stock - amount);
	}

	public boolean isSoldOut() {
		return this.stock == 0;
	}

}
