package com.example.green.domain.pointshop.entity.pointproduct;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.DisplayStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.global.error.exception.BusinessException;

class PointProductTest {

	static BasicInfo basicInfo;
	static Media media;
	static Price price;
	static Stock stock;
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
	void 새로운_포인트_상품을_생성하면_교환가능하고_전시상태이다() {
		// given when then
		assertThat(pointProduct).isNotNull();
		assertThat(pointProduct.getSellingStatus()).isEqualTo(SellingStatus.EXCHANGEABLE);
		assertThat(pointProduct.getDisplayStatus()).isEqualTo(DisplayStatus.DISPLAY);
	}

	@ParameterizedTest
	@MethodSource("invalidPointProductTestCases")
	void 상품_기본_정보는_필수_값이고_재고는_1개_이상이어야한다(BasicInfo basicInfo, Media media, Price price, Stock stock) {
		// given & when & then
		assertThatThrownBy(() -> PointProduct.create(basicInfo, media, price, stock))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 상품_기본_정보가_수정된다() {
		// given
		String newCode = "PRD-AA-002";
		String newName = "newName";
		String newDescription = "newDescription";
		BasicInfo newBasicInfo = new BasicInfo(newCode, newName, newDescription);

		// when
		pointProduct.updateBasicInfo(newBasicInfo);

		// then
		assertThat(pointProduct.getBasicInfo()).isEqualTo(newBasicInfo);
	}

	@Test
	void 상품_미디어_정보가_수정된다() {
		// given
		String newThumbnail = "https://example2.com/image.jpg";
		Media newMedia = new Media(newThumbnail);

		// when
		pointProduct.updateMedia(newMedia);

		// then
		assertThat(pointProduct.getMedia()).isEqualTo(newMedia);
	}

	@Test
	void 상품_포인트_정보가_수정된다() {
		// given
		Price newPrice = new Price(2000);

		// when
		pointProduct.updatePrice(newPrice);

		// then
		assertThat(pointProduct.getPrice()).isEqualTo(newPrice);
	}

	@Test
	void 상품_재고_정보가_수정된다() {
		// given
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);
		Stock newStock = new Stock(100);

		// when
		pointProduct.updateStock(newStock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(newStock);
	}

	@Test
	void 상품_재고가_0개로_수정되면_매진_상태이다() {
		// given
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);
		Stock newStock = new Stock(0);

		// when
		pointProduct.updateStock(newStock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(newStock);
		assertThat(pointProduct.getSellingStatus()).isEqualTo(SellingStatus.SOLD_OUT);
	}

	@Test
	void 상품이_매진_상태일_때_1개_이상으로_수정하면_교환_가능상태이다() {
		// given
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);
		pointProduct.updateStock(new Stock(0));
		Stock newStock = new Stock(1);

		// when
		pointProduct.updateStock(newStock);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(newStock);
		assertThat(pointProduct.getSellingStatus()).isEqualTo(SellingStatus.EXCHANGEABLE);
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
	void 상품_재고가_감소시_재고가_남아있지_않다면_매진_상태가_된다() {
		// given
		Stock stock = new Stock(100);
		PointProduct pointProduct = PointProduct.create(basicInfo, media, price, stock);
		int amount = 100;

		// when
		pointProduct.decreaseStock(amount);

		// then
		assertThat(pointProduct.getStock()).isEqualTo(new Stock(0));
		assertThat(pointProduct.getSellingStatus()).isEqualTo(SellingStatus.SOLD_OUT);
	}

	@Test
	void 상품을_미전시_한다() {
		// when
		pointProduct.hideDisplay();

		// then
		assertThat(pointProduct.getDisplayStatus()).isEqualTo(DisplayStatus.HIDDEN);
	}

	@Test
	void 미전시된_상품을_전시한다() {
		// given
		pointProduct.hideDisplay();

		// when
		pointProduct.showDisplay();

		// then
		assertThat(pointProduct.getDisplayStatus()).isEqualTo(DisplayStatus.DISPLAY);
	}

	static Stream<Arguments> invalidPointProductTestCases() {
		return Stream.of(
			Arguments.of(null, media, price, stock),
			Arguments.of(basicInfo, null, price, stock),
			Arguments.of(basicInfo, media, null, stock),
			Arguments.of(basicInfo, media, price, null),
			Arguments.of(basicInfo, media, price, new Stock(0))
		);
	}
}