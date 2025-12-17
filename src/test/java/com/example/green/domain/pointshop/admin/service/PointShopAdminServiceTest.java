package com.example.green.domain.pointshop.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.admin.dto.request.AdminCreatePointShopRequest;
import com.example.green.domain.pointshop.admin.dto.request.AdminUpdatePointShopRequest;
import com.example.green.domain.pointshop.admin.exception.PointShopAdminException;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.green.domain.pointshop.product.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.product.service.command.PointProductUpdateCommand;

@ExtendWith(MockitoExtension.class)
class PointShopAdminServiceTest {

	@InjectMocks
	private PointShopAdminService pointShopAdminService;

	@Mock
	private PointItemService pointItemService;

	@Mock
	private PointProductService pointProductService;

	@Mock
	private PointItemRepository pointItemRepository;

	@Mock
	private PointProductRepository pointProductRepository;

	@Test
	void ITEM_타입_포인트샵_생성에_성공한다() {
		// given
		AdminCreatePointShopRequest request = AdminCreatePointShopRequest.builder()
			.type(PointShopType.ITEM)
			.code("ITM-AA-000")
			.name("아이템")
			.description("아이템 설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(1000))
			.build();

		when(pointItemService.create(any(PointItemCreateCommand.class)))
			.thenReturn(1L);

		// when
		Long result = pointShopAdminService.create(request);

		// then
		assertThat(result).isEqualTo(1L);
		verify(pointItemService).create(any(PointItemCreateCommand.class));
		verify(pointProductService, never()).create(any());
	}

	@Test
	void PRODUCT_타입_포인트샵_생성에_성공한다() {
		// given
		AdminCreatePointShopRequest request = AdminCreatePointShopRequest.builder()
			.type(PointShopType.PRODUCT)
			.code("PRD-AA-000")
			.name("상품")
			.description("상품 설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(2000))
			.stock(10)
			.build();

		when(pointProductService.create(any(PointProductCreateCommand.class)))
			.thenReturn(2L);

		// when
		Long result = pointShopAdminService.create(request);

		// then
		assertThat(result).isEqualTo(2L);
		verify(pointProductService).create(any(PointProductCreateCommand.class));
		verify(pointItemService, never()).create(any());
	}

	@Test
	void 수정시_ITEM이_존재하면_ITEM_수정이_호출된다() {
		// given
		AdminUpdatePointShopRequest request = AdminUpdatePointShopRequest.builder()
			.code("ITM-AA-000")
			.name("아이템 수정")
			.description("설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(1500))
			.build();

		when(pointItemRepository.findById(1L))
			.thenReturn(Optional.of(mock(PointItem.class)));

		// when
		pointShopAdminService.update(request, 1L);

		// then
		verify(pointItemService)
			.updatePointItem(any(PointItemUpdateCommand.class), eq(1L));
		verify(pointProductService, never())
			.update(any(), anyLong());
	}

	@Test
	void 수정시_ITEM이_없고_PRODUCT가_존재하면_PRODUCT_수정이_호출된다() {
		// given
		AdminUpdatePointShopRequest request = AdminUpdatePointShopRequest.builder()
			.code("PRD-AA-000")
			.name("상품 수정")
			.description("설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(3000))
			.stock(5)
			.build();

		when(pointItemRepository.findById(1L))
			.thenReturn(Optional.empty());
		when(pointProductRepository.findById(1L))
			.thenReturn(Optional.of(mock(PointProduct.class)));

		// when
		pointShopAdminService.update(request, 1L);

		// then
		verify(pointProductService)
			.update(any(PointProductUpdateCommand.class), eq(1L));
	}

	@Test
	void 수정시_ITEM과_PRODUCT가_모두_없으면_예외가_발생한다() {
		// given
		AdminUpdatePointShopRequest request = mock(AdminUpdatePointShopRequest.class);

		when(pointItemRepository.findById(1L)).thenReturn(Optional.empty());
		when(pointProductRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> pointShopAdminService.update(request, 1L))
			.isInstanceOf(PointShopAdminException.class);
	}

	@Test
	void 삭제시_ITEM이_존재하면_ITEM_삭제가_호출된다() {
		// given
		when(pointItemRepository.findById(1L))
			.thenReturn(Optional.of(mock(PointItem.class)));

		// when
		pointShopAdminService.delete(1L);

		// then
		verify(pointItemService).delete(1L);
		verify(pointProductService, never()).delete(anyLong());
	}

	@Test
	void 삭제시_PRODUCT가_존재하면_PRODUCT_삭제가_호출된다() {
		// given
		when(pointItemRepository.findById(1L))
			.thenReturn(Optional.empty());
		when(pointProductRepository.findById(1L))
			.thenReturn(Optional.of(mock(PointProduct.class)));

		// when
		pointShopAdminService.delete(1L);

		// then
		verify(pointProductService).delete(1L);
	}

	@Test
	void 삭제시_ITEM과_PRODUCT가_모두_없으면_예외가_발생한다() {
		// given
		when(pointItemRepository.findById(1L)).thenReturn(Optional.empty());
		when(pointProductRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> pointShopAdminService.delete(1L))
			.isInstanceOf(PointShopAdminException.class);
	}
}
