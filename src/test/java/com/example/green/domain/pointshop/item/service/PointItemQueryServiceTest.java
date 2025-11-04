package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;

@ExtendWith(MockitoExtension.class)
class PointItemQueryServiceTest {

	@Mock
	private PointItemRepository repository;

	@InjectMocks
	private PointItemQueryService service;

	@Test
	void 아이템ID으로_아이템_가져온다() {
		//given
		PointItem dummy = mock(PointItem.class);
		when(repository.findById(anyLong())).thenReturn(Optional.of(dummy));

		//when
		PointItem pointItem = service.getPointItem(1L);

		assertThat(pointItem).isEqualTo(dummy);
	}

	@Test
	void 아이템_없는경우_예외발생() {
		//given
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		//when&then
		assertThatThrownBy(() -> service.getPointItem(1L))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.NOT_FOUND_ITEM.getMessage());

	}

	@Test
	void 아이템_코드가_동일한게_있다면_예외발생() {
		//given
		ItemCode dummyCode = mock(ItemCode.class);
		when(repository.existsByItemCodeAndIdNot(any(ItemCode.class), anyLong())).thenReturn(true);

		//when & then
		assertThatThrownBy(() -> service.validateUniqueCodeForUpdate(dummyCode, 1L))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.DUPLICATE_POINT_ITEM_CODE.getMessage());
	}

	@Test
	void 아이템_상세_정보_조회() {
		//given
		PointItem dummyEntity = spy(
			new PointItem(
				new ItemCode("ITM-AA-002"),
				new ItemBasicInfo("무지개 ", "무지개가 식물을 감싸요. 날씨가 좋을 것 같은 하루!"),
				new ItemMedia("https://thumbnail.url/image.jpg"),
				new ItemPrice(BigDecimal.valueOf(450))
			));
		//when
		when(repository.findById(anyLong())).thenReturn(Optional.of(dummyEntity));
		PointItem pointItem = service.getPointItem(1L);

		assertThat(pointItem.getItemBasicInfo().getItemName()).isEqualTo(dummyEntity.getItemBasicInfo().getItemName());
		assertThat(pointItem.getItemBasicInfo().getDescription()).isEqualTo(
			dummyEntity.getItemBasicInfo().getDescription());
		assertThat(pointItem.getItemPrice().getItemPrice()).isEqualTo(dummyEntity.getItemPrice().getItemPrice());
	}

}
