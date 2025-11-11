package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;
import com.example.green.domain.member.repository.MemberRepository;
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

	@Test
	void 포인트_아이템_교환_성공() {
		// given
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");
		PointItemSnapshot pointItemSnapshot = new PointItemSnapshot(
			1L, "ITM-AA-002", "무지개", "https://thumbnail.url/image.jpg", BigDecimal.valueOf(450)
		);
		OrderPointItemCommand command = new OrderPointItemCommand(memberSnapshot, pointItemSnapshot);

		Instant fixedInstant = Instant.now();
		Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

		given(memberRepository.existsByMemberKey(anyString())).willReturn(true);
		given(pointItemRepository.findById(anyLong()))
			.willReturn(Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class)));
		given(pointClient.getTotalPoints(anyLong())).willReturn(BigDecimal.valueOf(500));
		given(pointItemOrderRepository.existsByMemberIdAndPointItemId(anyLong(), anyLong())).willReturn(false);
		given(timeUtils.now()).willReturn(LocalDateTime.now(fixedClock));

		// when
		OrderPointItemResponse response = pointItemOrderService.orderPointItem(command);

		// then
		assertThat(response.memberId()).isEqualTo(1L);
		assertThat(response.itemName()).isEqualTo("무지개");
		assertThat(response.remainPoint()).isEqualByComparingTo(BigDecimal.valueOf(50));
	}

	@Test
	void 포인트_부족시_예외() {
		// given
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");
		PointItemSnapshot pointItemSnapshot = new PointItemSnapshot(
			1L, "ITM-AA-002", "무지개", "https://thumbnail.url/image.jpg", BigDecimal.valueOf(100)
		);
		OrderPointItemCommand command = mock(OrderPointItemCommand.class);
		when(command.memberSnapshot()).thenReturn(memberSnapshot);
		when(command.pointItemSnapshot()).thenReturn(pointItemSnapshot);

		when(memberRepository.existsByMemberKey(anyString())).thenReturn(true);
		when(pointItemRepository.findById(anyLong())).thenReturn(
			java.util.Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class)));
		when(pointClient.getTotalPoints(anyLong())).thenReturn(BigDecimal.valueOf(50));

		// when & then
		assertThatThrownBy(() -> pointItemOrderService.orderPointItem(command))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM.getMessage());
	}

	@Test
	void 이미_구매한_아이템일_경우_예외() {
		// given
		MemberSnapshot memberSnapshot = new MemberSnapshot(1L, "member_key", "user1@test.com");
		PointItemSnapshot pointItemSnapshot = new PointItemSnapshot(
			1L, "ITM-AA-002", "무지개", "https://thumbnail.url/image.jpg", BigDecimal.valueOf(100)
		);
		OrderPointItemCommand command = mock(OrderPointItemCommand.class);
		when(command.memberSnapshot()).thenReturn(memberSnapshot);
		when(command.pointItemSnapshot()).thenReturn(pointItemSnapshot);

		when(memberRepository.existsByMemberKey(anyString())).thenReturn(true);
		when(pointItemRepository.findById(anyLong())).thenReturn(
			java.util.Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class)));
		when(pointClient.getTotalPoints(anyLong())).thenReturn(BigDecimal.valueOf(200));
		when(pointItemOrderRepository.existsByMemberIdAndPointItemId(anyLong(), anyLong())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> pointItemOrderService.orderPointItem(command))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.ALREADY_PURCHASED_ITEM.getMessage());
	}

}
