package com.example.green.infra.query.pointproduct;

import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.domain.pointshop.product.entity.QPointProduct;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointProductProjections {

	public static QBean<PointProductSearchResult> toSearchResponse(QPointProduct qPointProduct) {
		return Projections.fields(
			PointProductSearchResult.class,
			qPointProduct.code.code.as("code"),
			qPointProduct.basicInfo.name.as("name"),
			qPointProduct.price.price.as("pointPrice"),
			qPointProduct.stock.stock.as("stockQuantity"),
			qPointProduct.sellingStatus,
			qPointProduct.displayStatus,
			qPointProduct.createdDate
		);
	}

	public static ConstructorExpression<PointProductView> toProductsView(QPointProduct qPointProduct) {
		return Projections.constructor(
			PointProductView.class,
			qPointProduct.id,
			qPointProduct.basicInfo.name,
			qPointProduct.media.thumbnailUrl,
			qPointProduct.price.price,
			qPointProduct.sellingStatus
		);
	}
}
