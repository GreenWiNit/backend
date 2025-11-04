package com.example.green.domain.pointshop.item.dto.response;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointItemClientResponse {

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String thumbnail;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private BigDecimal enablePoint;

	@Column(nullable = false)
	private BigDecimal decreasePoint;

	@Column(nullable = false)
	private BigDecimal remainPoint;

	public PointItemClientResponse(
		String itemName,
		String description,
		String thumbnail,
		BigDecimal price,
		BigDecimal enablePoint,
		BigDecimal decreasePoint,
		BigDecimal remainPoint
	) {
		validateEmptyString(itemName, "아이템 이름은 필수 값입니다.");
		validateEmptyString(description, "아이템 설명은 필수 값입니다.");
		validateEmptyString(thumbnail, "아이템 썸네일 이미지는 필수 값입니다. ");
		validateNullData(price, "아이템 가격은 필수 값입니다.");
		validateNullData(enablePoint, "사용 가능한 포인트는 필수 값입니다.");
		validateNullData(decreasePoint, "차감 포인트는 필수 값입니다.");
		validateNullData(remainPoint, "남은 포인트는 필수 값입니다.");

		this.itemName = itemName;
		this.description = description;
		this.thumbnail = thumbnail;
		this.price = price;
		this.enablePoint = enablePoint;
		this.decreasePoint = decreasePoint;
		this.remainPoint = remainPoint;
	}
}
