package com.example.green.domain.pointshop.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;

@ExtendWith(MockitoExtension.class)
class PointProductDomainServiceTest {

	@Mock
	private PointProductRepository pointProductRepository;
	@InjectMocks
	private PointProductDomainService pointProductDomainService;

	@Test
	void 포인트_상품을_가져온다() {
		// given
		PointProduct mockPointProduct = mock(PointProduct.class);
		when(pointProductRepository.findById(anyLong())).thenReturn(Optional.of(mockPointProduct));

		// when
		PointProduct pointProduct = pointProductDomainService.getPointProduct(1L);

		// then
		assertThat(pointProduct).isEqualTo(mockPointProduct);
	}

	@Test
	void 포인트_상품이_없다면_예외가_발생한다() {
		// given
		when(pointProductRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> pointProductDomainService.getPointProduct(1L))
			.isInstanceOf(PointProductException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", NOT_FOUND_POINT_PRODUCT);
	}

	@Test
	void 업데이트_상품을_제외한_상품_중_상품_코드가_동일한게_있다면_예외가_발생한다() {
		// given
		Code mockCode = mock(Code.class);
		when(pointProductRepository.existsByCodeAndIdNot(any(Code.class), anyLong())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> pointProductDomainService.validateUniqueCodeForUpdate(mockCode, 1L))
			.isInstanceOf(PointProductException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", DUPLICATE_POINT_PRODUCT_CODE);
	}

	@Test
	void 업데이트_상품을_제외한_상품_중_상품_코드가_동일한게_없다면_예외가_발생하지_않는다() {
		// given
		Code mockCode = mock(Code.class);
		when(pointProductRepository.existsByCodeAndIdNot(any(Code.class), anyLong())).thenReturn(false);

		// when & then
		assertThatCode(() -> pointProductDomainService.validateUniqueCodeForUpdate(mockCode, 1L))
			.doesNotThrowAnyException();
	}
}