package com.example.green.domain.pointshop.service;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.repository.PointProductRepository;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.service.command.PointProductUpdateCommand;

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
		when(pointProductRepository.existsByBasicInfoCode(anyString())).thenReturn(false);
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
		when(pointProductRepository.existsByBasicInfoCode(anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> pointProductService.create(command))
			.isInstanceOf(PointProductException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", EXISTS_PRODUCT_CODE);
	}

	void 포인트_상품_수정시_새로운_이미지가_아니면_기본_정보만_변경된다() {
		// given
		PointProductUpdateCommand command = getUpdateCommand();
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductDomainService.getPointProduct(anyLong())).thenReturn(mockPointProduct);
		when(mockPointProduct.isNewImage(command.media())).thenReturn(false);

		// when
		pointProductService.update(command, 1L);

		// then
		verify(pointProductDomainService).validateUniqueCodeForUpdate(command.basicInfo().getCode(), 1L);
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

	private PointProductUpdateCommand getUpdateCommand() {
		return new PointProductUpdateCommand(
			new BasicInfo("PRD-AA-001", "상품명", "상품 소개"),
			new Media("https://thumbnail.url/image.jpg"),
			new Price(BigDecimal.valueOf(1000)),
			new Stock(50)
		);
	}

	private PointProductCreateCommand getCreateCommand() {
		return new PointProductCreateCommand(
			new BasicInfo("PRD-AA-001", "상품명", "상품 소개"),
			new Media("https://thumbnail.url/image.jpg"),
			new Price(BigDecimal.valueOf(1000)),
			new Stock(50)
		);
	}
}