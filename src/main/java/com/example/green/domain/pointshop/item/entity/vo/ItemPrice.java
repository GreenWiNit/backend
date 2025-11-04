package com.example.green.domain.pointshop.item.entity.vo;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
public class ItemPrice {

	@Column(nullable = false, precision = 19, scale = 2, name = "iterm_price")
	private BigDecimal itemPrice;

	public ItemPrice(BigDecimal itemPrice) {
		validateNullData(itemPrice, REQUIRED_ITEM_PRICE);
		if (itemPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new PointItemException(INVALID_ITEM_PRICE);
		}
		this.itemPrice = itemPrice.setScale(2, RoundingMode.DOWN);
	}
}
