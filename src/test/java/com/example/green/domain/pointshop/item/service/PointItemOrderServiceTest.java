package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.pointshop.item.dto.request.OrderPointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.OrderPointItemResponse;
import com.example.green.domain.pointshop.item.entity.vo.PointItemSnapshot;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemOrderRepository;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.command.OrderPointItemCommand;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.client.PointClient;
import com.example.green.infra.client.request.PointSpendRequest;

@ExtendWith(MockitoExtension.class)
class PointItemOrderServiceTest {

	@InjectMocks
	private PointItemOrderService pointItemOrderService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PointItemRepository pointItemRepository;

	@Mock
	private PointItemOrderRepository pointItemOrderRepository;

	@Mock
	private PlantGrowthItemRepository plantGrowthItemRepository;

	@Mock
	private PointClient pointClient;

	@Mock
	private TimeUtils timeUtils;

	@Mock
	private PointItemService pointItemService;

	private Member member;

	@BeforeEach
	void setUp() {
		member = Member.create("mb-1234", "이지은", "user1@test.com", "nickname");

		// 실무 테스트에서는 ReflectionTestUtils로 ID 설정
		ReflectionTestUtils.setField(member, "id", 1L);
	}

	@Test
	void 포인트_아이템_교환_성공() {
		// given
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");

		PointItemSnapshot pointItemSnapshot = new PointItemSnapshot(
			1L,
			"ITM-AA-002",
			"무지개",
			"https://thumbnail.url/image.jpg",
			BigDecimal.valueOf(450)
		);

		OrderPointItemCommand command = new OrderPointItemCommand(memberSnapshot, pointItemSnapshot);
		OrderPointItemRequest request = new OrderPointItemRequest(4); // 구매 수량

		// 현재 보유 포인트 (remain 50 되려면 = total 1850)
		given(pointClient.getTotalPoints(1L)).willReturn(BigDecimal.valueOf(1850));

		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(pointItemRepository.findById(anyLong()))
			.willReturn(Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class)));

		// Stock 감소 mock
		doNothing().when(pointItemService).decreaseItemStock(1L, 4);

		// spend 포인트 mock
		doNothing().when(pointClient).spendPoints(any(PointSpendRequest.class));

		// when
		OrderPointItemResponse response = pointItemOrderService.orderPointItem(command, request);

		// then
		assertThat(response.memberId()).isEqualTo(1L);
		assertThat(response.itemName()).isEqualTo("무지개");
		assertThat(response.remainPoint()).isEqualByComparingTo(BigDecimal.valueOf(50));

		// 양방향 관계 확인
		assertThat(member.getPlantGrowthItems().get(0).getMember()).isEqualTo(member);
	}

	@Test
	void 포인트_부족시_예외() {
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");
		PointItemSnapshot pointItemSnapshot = new PointItemSnapshot(
			1L, "ITM-AA-002", "무지개", "https://thumbnail.url/image.jpg", BigDecimal.valueOf(100)
		);
		OrderPointItemRequest request = new OrderPointItemRequest(4); // 구매 수량

		OrderPointItemCommand command = mock(OrderPointItemCommand.class);
		when(command.memberSnapshot()).thenReturn(memberSnapshot);
		when(command.pointItemSnapshot()).thenReturn(pointItemSnapshot);

		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(pointItemRepository.findById(anyLong())).willReturn(
			Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class))
		);
		given(pointClient.getTotalPoints(1L)).willReturn(BigDecimal.valueOf(50));

		assertThatThrownBy(() -> pointItemOrderService.orderPointItem(command, request))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM.getMessage());
	}

	@Test
	void 재고가_부족하면_예외발생() {
		// given
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");

		PointItemSnapshot snapshot = new PointItemSnapshot(
			1L,
			"무지개",
			"ITM-AA-002",
			"https://thumbnail.url/image.jpg",
			BigDecimal.valueOf(450)
		);

		OrderPointItemCommand command = new OrderPointItemCommand(memberSnapshot, snapshot);
		OrderPointItemRequest request = new OrderPointItemRequest(5); // 구매 요청 수량: 5

		// member mocking
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));

		// 아이템 존재
		given(pointItemRepository.findById(anyLong()))
			.willReturn(Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class)));

		// 포인트는 충분하다고 가정 (문제 관심사는 재고)
		given(pointClient.getTotalPoints(1L)).willReturn(BigDecimal.valueOf(9999));

		// 재고 부족 상황 mocking
		doThrow(new PointItemException(PointItemExceptionMessage.OUT_OF_ITEM_STOCK))
			.when(pointItemService)
			.decreaseItemStock(1L, 5);

		// when & then
		assertThatThrownBy(() -> pointItemOrderService.orderPointItem(command, request))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.OUT_OF_ITEM_STOCK.getMessage());

		// spendPoints, save 등 → 호출되면 안 됨
		verify(pointClient, never()).spendPoints(any());
		verify(pointItemOrderRepository, never()).save(any());
	}

}
