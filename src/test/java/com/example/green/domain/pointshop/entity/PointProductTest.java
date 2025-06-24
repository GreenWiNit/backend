package com.example.green.domain.pointshop.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;

class PointProductTest {

	String code = "PRD-AA-001";
	String name = "name";
	String description = "description";
	String thumbnail = "http://example.com/image.jpg";
	int point = 1000;
	int stock = 100;

	@Test
	void 새로운_포인트_상품을_생성하면_판매상태이고_전시상태이다() {
		// given
		// when
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, point, stock);

		// then
		assertThat(pointProduct).isNotNull();
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.IN_STOCK);
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}
}