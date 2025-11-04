package com.example.green.domain.pointshop.item.dto.response;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointItemAdminResponse {

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String thumbnail;

	@Column(nullable = false)
	private BigDecimal price;

	public PointItemAdminResponse(
		String itemName,
		String description,
		String thumbnail,
		BigDecimal price
	) {
		validateEmptyString(itemName, "아이템 이름은 필수 값입니다.");
		validateEmptyString(description, "아이템 설명은 필수 값입니다.");
		validateEmptyString(thumbnail, "아이템 썸네일 이미지는 필수 값입니다. ");
		validateNullData(price, "아이템 가격은 필수 값입니다.");

		this.itemName = itemName;
		this.description = description;
		this.thumbnail = thumbnail;
		this.price = price;

	}
}
