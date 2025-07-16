package com.example.green.domain.pointshop.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.domain.pointshop.product.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.product.service.command.PointProductUpdateCommand;

@ExtendWith(MockitoExtension.class)
class PointProductServiceTest {

	@Mock
	private PointProductDomainService pointProductDomainService;
	@Mock
	private PointProductRepository pointProductRepository;
	@Mock
	private FileManager fileManager;

	@InjectMocks
	private PointProductService pointProductService;

	@Test
	void 포인트_상품을_생성하고_등록한다() {
		// given
		PointProductCreateCommand command = getCreateCommand();
		PointProduct mockEntity = mock(PointProduct.class);
		when(pointProductRepository.existsByCode(any(Code.class))).thenReturn(false);
		when(mockEntity.getId()).thenReturn(1L);
		when(pointProductRepository.save(any(PointProduct.class))).thenReturn(mockEntity);

		// when
		Long result = pointProductService.create(command);

		// then
		assertThat(result).isEqualTo(1L);
	}

	@Test
	void 포인트_상품_생성시_중복된_코드가_존재하면_예외가_발생한다() {
		// given
		PointProductCreateCommand command = getCreateCommand();
		when(pointProductRepository.existsByCode(any(Code.class))).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> pointProductService.create(command))
			.isInstanceOf(PointProductException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", EXISTS_PRODUCT_CODE);
	}

	@Test
	void 포인트_상품_수정시_새로운_이미지가_아니면_기본_정보만_변경된다() {
		// given
		PointProductUpdateCommand command = getUpdateCommand();
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);
		when(mockPointProduct.isNewImage(command.media())).thenReturn(false);

		// when
		pointProductService.update(command, 1L);

		// then
		verify(pointProductDomainService).validateUniqueCodeForUpdate(command.code(), 1L);
		verify(mockPointProduct).updateBasicInfo(command.basicInfo());
		verify(mockPointProduct).updatePrice(command.price());
		verify(mockPointProduct).updateStock(command.stock());
	}

	@Test
	void 포인트_상품_수정시_새로운_이미지라면_이미지_정보가_수정되고_사용_및_미사용_처리한다() {
		// given
		PointProductUpdateCommand command = getUpdateCommand();
		PointProduct mockPointProduct = mock(PointProduct.class);
		String oldImageUrl = "oldImageUrl";
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);
		when(mockPointProduct.isNewImage(command.media())).thenReturn(true);
		when(mockPointProduct.getThumbnailUrl()).thenReturn(oldImageUrl)
			.thenReturn(command.media().getThumbnailUrl());

		// when
		pointProductService.update(command, 1L);

		// then
		verify(fileManager).unUseImage(oldImageUrl);
		verify(mockPointProduct).updateMedia(command.media());
		verify(fileManager).confirmUsingImage(command.media().getThumbnailUrl());
	}

	@Test
	void 포인트_상품을_삭제한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.delete(1L);

		// then
		verify(mockPointProduct).markDeleted();
	}

	@Test
	void 포인트_상품을_전시한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.showDisplay(1L);

		// then
		verify(mockPointProduct).showDisplay();
	}

	@Test
	void 포인트_상품을_미전시_처리한다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

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
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);
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
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);

		// when
		pointProductService.decreaseSingleItemStock(1L, 10);

		// then
		verify(mockPointProduct).decreaseStock(10);
	}

	private PointProductUpdateCommand getUpdateCommand() {
		return new PointProductUpdateCommand(
			new Code("PRD-AA-001"),
			new BasicInfo("상품명", "상품 소개"),
			new Media("https://thumbnail.url/image.jpg"),
			new Price(BigDecimal.valueOf(1000)),
			new Stock(50)
		);
	}

	private PointProductCreateCommand getCreateCommand() {
		return new PointProductCreateCommand(
			new Code("PRD-AA-001"),
			new BasicInfo("상품명", "상품 소개"),
			new Media("https://thumbnail.url/image.jpg"),
			new Price(BigDecimal.valueOf(1000)),
			new Stock(50)
		);
	}
}