package com.example.green.domain.pointshop.item.entity.vo;

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
public class PointItemSnapshot {

	@Column(nullable = false, name = "point_item_id")
	private Long pointItemId;

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private String itemCode;

	@Column(nullable = false)
	private String itemImgUrl;

	@Column(nullable = false)
	private BigDecimal itemPrice;

	public PointItemSnapshot(Long pointItemId, String itemCode, String itemName, String itemImgUrl,
		BigDecimal itemPrice) {
		validateAutoIncrementId(pointItemId, "아이템 ID는 필수 값입니다");
		validateEmptyString(itemName, "아이템 이름은 필수 값 입니다.");
		validateEmptyString(itemCode, "아이템 코드는 필수 값 입니다.");
		validateEmptyString(itemImgUrl, "아이템 사진 URL은 필수 값 입니다.");
		validateNullData(itemPrice, "아이템 단가는 필수 값 입니다.");
		this.pointItemId = pointItemId;
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.itemImgUrl = itemImgUrl;
		this.itemPrice = itemPrice;
	}

}
