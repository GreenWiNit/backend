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
	int price = 1000;
	int stock = 100;

	@Test
	void 새로운_포인트_상품을_생성하면_판매상태이고_전시상태이다() {
		// given
		// when
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);

		// then
		assertThat(pointProduct).isNotNull();
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.IN_STOCK);
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}

	@Test
	void 상품_기본_정보가_수정된다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		String newCode = "PRD-AA-002";
		String newName = "newName";
		String newDescription = "newDescription";

		// when
		pointProduct.updateBasicInfo(newCode, newName, newDescription);

		// then
		assertThat(pointProduct.getCode()).isEqualTo(newCode);
		assertThat(pointProduct.getName()).isEqualTo(newName);
		assertThat(pointProduct.getDescription()).isEqualTo(newDescription);
	}

	@Test
	void 상품_미디어_정보가_수정된다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		String newThumbnail = "https://example2.com/image.jpg";

		// when
		pointProduct.updateMedia(newThumbnail);

		// then
		assertThat(pointProduct.getThumbnailUrl()).isEqualTo(newThumbnail);
	}

	@Test
	void 상품_포인트_정보가_수정된다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		int newPrice = 2000;

		// when
		pointProduct.updatePrice(newPrice);

		// then
		assertThat(pointProduct.getPrice()).isEqualTo(newPrice);
	}

	@Test
	void 상품_재고_정보가_수정된다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		int newStock = 200;

		// when
		pointProduct.updateStock(newStock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(newStock);
	}

	@Test
	void 상품_재고가_감소한다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		int amount = 50;

		// when
		pointProduct.decreaseStock(amount);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(amount);
	}

	@Test
	void 상품_재고가_감소되고_0개만_남으면_매진_상태가_된다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);

		// when
		pointProduct.decreaseStock(stock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(0);
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.OUT_OF_STOCK);
	}

	@Test
	void 상품을_매진_처리한다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);

		// when
		pointProduct.soldOut();

		// then
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.OUT_OF_STOCK);
	}

	@Test
	void 상품을_미전시한다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);

		// when
		pointProduct.hide();

		// then
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.HIDDEN);
	}

	@Test
	void 상품을_재전시한다() {
		// given
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, price, stock);
		pointProduct.hide();

		// when
		pointProduct.show();

		// then
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}
}