package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
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
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
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
	void 포인트_아이템_상품을_생성하고_등록한다() {
		//given
		PointItemCreateCommand command = getCreateItemCommand();
		PointItem dummyEntity = mock(PointItem.class);
		when(pointItemRepository.existsByItemCode(any(ItemCode.class))).thenReturn(false);
		when(dummyEntity.getId()).thenReturn(1L);
		when(pointItemRepository.save(any(PointItem.class))).thenReturn(dummyEntity);

		//when
		Long result = pointItemService.create(command);

		//then
		assertThat(result).isEqualTo(1L);
		assertThat(command.info().getItemName()).isEqualTo("맑은 뭉게 구름");
		assertEquals(new BigDecimal("1200.00"), command.price().getItemPrice());
	}

	@Test
	void 포인트_아이템_상품_생성시_중복된_코드가_존재하면_예외발생() {
		//given
		PointItemCreateCommand command = getCreateItemCommand();
		when(pointItemRepository.existsByItemCode(any(ItemCode.class))).thenReturn(true);

		assertThatThrownBy(() -> pointItemService.create(command))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.EXISTS_ITEM_CODE.getMessage());
	}

	@Test
	void 포인트_아이템_상품_수정시_새로운_이미지가_아닌경우_기본정보만변경() {

		//given
		PointItemUpdateCommand command = getUpdateItemCommand();
		PointItem dummyEntity = spy(
			new PointItem(
				new ItemCode("ITM-AA-002"),
				new ItemBasicInfo("무지개 ", "무지개가 식물을 감싸요. 날씨가 좋을 것 같은 하루!"),
				new ItemMedia("https://thumbnail.url/image.jpg"),
				new ItemPrice(BigDecimal.valueOf(450))
			));
		when(pointItemQueryService.getPointItem(anyLong())).thenReturn(dummyEntity);
		when(dummyEntity.isNewImage(command.media())).thenReturn(false);

		//when
		pointItemService.updatePointItem(command, 1L);

		//then
		verify(pointItemQueryService).validateUniqueCodeForUpdate(command.itemCode(), 1L);
		assertThat(dummyEntity.getItemPrice()).isEqualTo(new ItemPrice(BigDecimal.valueOf(600)));
		assertThat(dummyEntity.getItemBasicInfo().getItemName()).isEqualTo("행운의 네잎클로버");
	}

	@Test
	void 포인트_아이템_상품_수정하는경우_새로운_이미지라면_이미지_정보가_수정되고_사용_및_미사용_처리한다() {
		PointItemUpdateCommand command = getUpdateItemCommand();
		PointItem dummyEntity = spy(
			new PointItem(
				new ItemCode("ITM-AA-002"),
				new ItemBasicInfo("무지개 ", "무지개가 식물을 감싸요. 날씨가 좋을 것 같은 하루!"),
				new ItemMedia("https://thumbnail.url/image.jpg"),
				new ItemPrice(BigDecimal.valueOf(450))
			));
		String oldImageUrl = "https://thumbnail.url/image.jpg";
		String newImageUrl = command.media().getItemThumbNailUrl();

		when(pointItemQueryService.getPointItem(anyLong())).thenReturn(dummyEntity);
		doReturn(true).when(dummyEntity).isNewImage(command.media());
		doReturn(oldImageUrl).when(dummyEntity).getThumbnailUrl();

		pointItemService.updatePointItem(command, 1L);

		verify(pointItemQueryService).validateUniqueCodeForUpdate(command.itemCode(), 1L);
		verify(fileClient).unUseImage(oldImageUrl); // 기존 이미지 비활성화
		verify(fileClient).confirmUsingImage(newImageUrl);  // 새 이미지 활성화
		verify(dummyEntity).updateItemMedia(command.media());   // 도메인 객체에 반영됨
	}

	@Test
	void 아이템_삭제한다() {
		//given
		PointItem dummyEntity = mock(PointItem.class);
		when(pointItemQueryService.getPointItem(anyLong())).thenReturn(dummyEntity);

		//when
		pointItemService.delete(1L);

		//then
		verify(dummyEntity).markDeleted();
	}

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

	private PointItemCreateCommand getCreateItemCommand() {
		return new PointItemCreateCommand(
			new ItemCode("ITM-AA-001"),
			new ItemBasicInfo("맑은 뭉게 구름 ", "하늘에서 포근한 구름이 내려와 식물을 감싸요. 몽글몽글 기분 좋은 하루!"),
			new ItemMedia("https://thumbnail.url/image.jpg"),
			new ItemPrice(BigDecimal.valueOf(1200))
		);
	}

	private PointItemUpdateCommand getUpdateItemCommand() {
		return new PointItemUpdateCommand(
			new ItemCode("ITM-AA-001"),
			new ItemBasicInfo("행운의 네잎클로버 ", "행운의 네잎클로버가 식물을 감싸요. 뭔가 운이 좋을 것 같은 하루!"),
			new ItemMedia("https://thumbnail.url/image.jpg"),
			new ItemPrice(BigDecimal.valueOf(600)));
	}
}
