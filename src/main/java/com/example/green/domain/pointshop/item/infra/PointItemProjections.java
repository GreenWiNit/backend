package com.example.green.domain.pointshop.item.infra;

import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.entity.QPointItem;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointItemProjections {

	public static QBean<PointItemSearchResponse> toSearchItemResponse(QPointItem qPointItem) {
		return Projections.fields(
			PointItemSearchResponse.class,
			qPointItem.id.as("id"),
			qPointItem.itemCode.code.as("code"),
			qPointItem.itemBasicInfo.itemName.as("name"),
			qPointItem.itemPrice.itemPrice.as("pointPrice"),
			qPointItem.displayStatus,
			qPointItem.createdDate
		);
	}

	public static QBean<PointItemResponse> toPointItemView(QPointItem qPointItem) {
		return Projections.fields(
			PointItemResponse.class,
			qPointItem.id.as("pointItemId"),
			qPointItem.itemBasicInfo.itemName.as("pointItemName"),
			qPointItem.itemMedia.itemThumbNailUrl.as("thumbnailUrl"),
			qPointItem.itemPrice.itemPrice.as("pointPrice")
		);
	}

}
