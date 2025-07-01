package com.example.green.infra.query.pointproduct;

import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.entity.pointproduct.QPointProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointProductProjections {

	public static QBean<PointProductSearchResponse> toSearchResponse(QPointProduct qPointProduct) {
		return Projections.fields(
			PointProductSearchResponse.class,
			qPointProduct.basicInfo.code.as("code"),
			qPointProduct.basicInfo.name.as("name"),
			qPointProduct.price.price.as("pointPrice"),
			qPointProduct.stock.stock.as("stockQuantity"),
			qPointProduct.sellingStatus,
			qPointProduct.displayStatus,
			qPointProduct.createdDate
		);
	}
}
