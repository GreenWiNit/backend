package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.item.dto.response.UserPointCalculation;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.infra.client.FileClient;

@ExtendWith(MockitoExtension.class)
class PointItemServiceTest {

	@Mock
	private PointItemQueryService pointItemQueryService;

	@Mock
	private PointItemRepository pointItemRepository;

	@Mock
	private FileClient fileClient;

	@InjectMocks
	private PointItemService pointItemService;

	@Test
	void 로그인_안한_사용자_아이템_조회() {
		// given
		Long itemId = 1L;
		BigDecimal price = BigDecimal.valueOf(500);

		PointItem dummyEntity = mock(PointItem.class);

		when(pointItemQueryService.getPointItem(itemId)).thenReturn(dummyEntity);
		when(dummyEntity.getItemPrice()).thenReturn(new ItemPrice(price));
		when(dummyEntity.getItemBasicInfo()).thenReturn(
			new ItemBasicInfo("테스트아이템", "설명입니다.")
		);
		when(dummyEntity.getItemMedia()).thenReturn(
			new ItemMedia("https://thumbnail.url/image.jpg")
		);
		// when
		var response = pointItemService.getPointItemInfo(null, itemId);

		// then
		assertThat(response.getEnablePoint()).isEqualTo(BigDecimal.ZERO);
		assertThat(response.getDecreasePoint()).isEqualByComparingTo(price);
		assertThat(response.getRemainPoint()).isEqualTo(BigDecimal.ZERO);

		// 포인트 계산 로직이 호출되면 안됨
		verify(pointItemQueryService, never()).userPointsCalculate(any(), any());
	}

	@Test
	void 로그인한_사용자_아이템_조회() {
		// given
		Long itemId = 1L;
		Long memberId = 10L;

		PointItem dummyEntity = mock(PointItem.class);

		when(pointItemQueryService.getPointItem(itemId)).thenReturn(dummyEntity);
		when(dummyEntity.getItemPrice()).thenReturn(new ItemPrice(BigDecimal.valueOf(1000)));
		when(dummyEntity.getItemBasicInfo()).thenReturn(
			new ItemBasicInfo("테스트아이템", "설명입니다.")
		);
		when(dummyEntity.getItemMedia()).thenReturn(
			new ItemMedia("https://thumbnail.url/image.jpg")
		);
		// 포인트 계산 결과(record)
		UserPointCalculation calc = new UserPointCalculation(
			BigDecimal.valueOf(5000),
			BigDecimal.valueOf(1000),
			BigDecimal.valueOf(4000)
		);

		when(pointItemQueryService.userPointsCalculate(memberId, itemId))
			.thenReturn(calc);

		// when
		var response = pointItemService.getPointItemInfo(memberId, itemId);

		// then
		assertThat(response.getEnablePoint()).isEqualTo(BigDecimal.valueOf(5000));
		assertThat(response.getDecreasePoint()).isEqualTo(BigDecimal.valueOf(1000));
		assertThat(response.getRemainPoint()).isEqualTo(BigDecimal.valueOf(4000));

		verify(pointItemQueryService).userPointsCalculate(memberId, itemId);
	}
}
