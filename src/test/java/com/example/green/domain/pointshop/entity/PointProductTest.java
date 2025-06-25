package com.example.green.domain.pointshop.entity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.pointshop.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.vo.Media;
import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;
import com.example.green.domain.pointshop.entity.vo.Price;
import com.example.green.domain.pointshop.entity.vo.Stock;

class PointProductTest {

	BasicInfo basicInfo;
	Media media;
	Price price;
	Stock stock;
	PointProduct pointProduct;

	@BeforeEach
	void setUp() {
		basicInfo = mock(BasicInfo.class);
		media = mock(Media.class);
		price = mock(Price.class);
		stock = mock(Stock.class);
		pointProduct = PointProduct.create(basicInfo, media, price, stock);
	}

	@Test
	void 새로운_포인트_상품을_생성하면_판매상태이고_전시상태이다() {
		// given when then
		assertThat(pointProduct).isNotNull();
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.IN_STOCK);
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}

	@Test
	void 상품_기본_정보가_수정된다() {
		// given
		String newCode = "PRD-AA-002";
		String newName = "newName";
		String newDescription = "newDescription";

		// when
		pointProduct.updateBasicInfo(newCode, newName, newDescription);

		// then
		assertThat(pointProduct.getBasicInfo()).isEqualTo(new BasicInfo(newCode, newName, newDescription));
	}

	@Test
	void 상품_미디어_정보가_수정된다() {
		// given
		String newThumbnail = "https://example2.com/image.jpg";

		// when
		pointProduct.updateMedia(newThumbnail);

		// then
		assertThat(pointProduct.getMedia()).isEqualTo(new Media(newThumbnail));
	}

	@Test
	void 상품_포인트_정보가_수정된다() {
		// given
		int newPrice = 2000;

		// when
		pointProduct.updatePrice(newPrice);

		// then
		assertThat(pointProduct.getPrice()).isEqualTo(new Price(newPrice));
	}

	@Test
	void 상품_재고_정보가_수정된다() {
		// given
		int newStock = 200;

		// when
		pointProduct.updateStock(newStock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(new Stock(newStock));
	}

	@Test
	void 상품_재고가_감소한다() {
		// given
		Stock stock = new Stock(100);
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);
		int amount = 50;

		// when
		pointProduct.decreaseStock(amount);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(new Stock(50));
	}

	@Test
	void 상품_재고가_감소되고_0개만_남으면_매진_상태가_된다() {
		// given
		Stock stock = new Stock(100);
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);

		// when
		pointProduct.decreaseStock(100);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(new Stock(0));
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.OUT_OF_STOCK);
	}

	@Test
	void 상품을_매진_처리한다() {
		// when
		pointProduct.markAsSoldOut();

		// then
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.OUT_OF_STOCK);
	}

	@Test
	void 매진된_상품을_다시_교환_가능_상태로_처리한다() {
		// given
		pointProduct.markAsSoldOut();

		// when
		pointProduct.backInStock();

		// then
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.IN_STOCK);
	}

	@Test
	void 상품을_미전시한다() {
		// when
		pointProduct.hide();

		// then
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.HIDDEN);
	}

	@Test
	void 상품을_재전시한다() {
		// given
		pointProduct.hide();

		// when
		pointProduct.show();

		// then
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}
}