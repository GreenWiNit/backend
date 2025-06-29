package com.example.green.domain.pointshop.entity.order.vo;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSnapshot {

	@Column(nullable = false, name = "point_product_id")
	private Long itemId;
	@Column(nullable = false)
	private String itemCode;
	@Column(nullable = false)
	private BigDecimal unitPrice;

	public ItemSnapshot(Long itemId, String itemCode, BigDecimal unitPrice) {
		validateAutoIncrementId(itemId, "아이템 ID는 필수 값 입니다.");
		validateEmptyString(itemCode, "아이템 코드는 필수 값 입니다.");
		validateNullData(unitPrice, "아이템 단가는 필수 값 입니다.");
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.unitPrice = unitPrice;
	}
}
