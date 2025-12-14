package com.example.green.domain.pointshop.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.infra.client.FileClient;

@ExtendWith(MockitoExtension.class)
class PointProductServiceTest {

	@Mock
	private PointProductQueryService pointProductQueryService;
	@Mock
	private PointProductRepository pointProductRepository;
	@Mock
	private FileClient fileClient;

	@InjectMocks
	private PointProductService pointProductService;

	@Test
	void 포인트_상품을_전시한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductQueryService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.showDisplay(1L);

		// then
		verify(mockPointProduct).showDisplay();
	}

	@Test
	void 포인트_상품을_미전시_처리한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductQueryService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.hideDisplay(1L);

		// then
		verify(mockPointProduct).hideDisplay();
	}

	@Test
	void 포인트_상품_ID로_스냅샷을_가져올_수_있다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		BasicInfo mockBasicInfo = mock(BasicInfo.class);
		Price mockPrice = mock(Price.class);
		Code mockCode = mock(Code.class);
		when(mockBasicInfo.getName()).thenReturn("name");
		when(mockPrice.getPrice()).thenReturn(new BigDecimal("100"));
		when(mockCode.getCode()).thenReturn("code");
		when(pointProductQueryService.getPointProduct(anyLong())).thenReturn(mockPointProduct);
		when(mockPointProduct.getId()).thenReturn(1L);
		when(mockPointProduct.getBasicInfo()).thenReturn(mockBasicInfo);
		when(mockPointProduct.getCode()).thenReturn(mockCode);
		when(mockPointProduct.getPrice()).thenReturn(mockPrice);

		// when
		ItemSnapshot itemSnapshot = pointProductService.getItemSnapshot(1L);

		// then
		assertThat(itemSnapshot.getItemCode()).isEqualTo("code");
		assertThat(itemSnapshot.getItemName()).isEqualTo("name");
		assertThat(itemSnapshot.getItemId()).isEqualTo(1L);
		assertThat(itemSnapshot.getUnitPrice()).isEqualTo(new BigDecimal("100"));
	}

	@Test
	void 포인트_상품_아이디와_수량으로_재고를_감소한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductQueryService.getPointProductWithPessimisticLock(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.decreaseSingleItemStock(1L, 10);

		// then
		verify(mockPointProduct).decreaseStock(10);
	}
}
