package com.example.green.domain.pointshop.item.dto.response;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.item.entity.PointItem;

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
		validateEmptyString(itemName, REQUIRED_ITEM_NAME);
		validateEmptyString(description, REQUIRED_ITEM_DESCRIPTION);
		validateEmptyString(thumbnail, REQUIRED_ITEM_MEDIA);
		validateNullData(price, REQUIRED_ITEM_PRICE);
		validateNullData(enablePoint, REQUIRED_ENABLE_POINT);
		validateNullData(decreasePoint, REQUIRED_DECREASE_POINT);
		validateNullData(remainPoint, REQUIRED_REMAIN_POINT);

		this.itemName = itemName;
		this.description = description;
		this.thumbnail = thumbnail;
		this.price = price;
		this.enablePoint = enablePoint;
		this.decreasePoint = decreasePoint;
		this.remainPoint = remainPoint;
	}

	public static PointItemClientResponse from(PointItem pointItem, BigDecimal enablePoint, BigDecimal decreasePoint,
		BigDecimal remainPoint) {
		return new PointItemClientResponse(
			pointItem.getItemBasicInfo().getItemName(),
			pointItem.getItemBasicInfo().getDescription(),
			pointItem.getItemMedia().getItemThumbNailUrl(),
			pointItem.getItemPrice().getItemPrice(),
			enablePoint,
			decreasePoint,
			remainPoint
		);
	}
}
