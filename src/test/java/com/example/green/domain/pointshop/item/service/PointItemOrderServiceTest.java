package com.example.green.domain.pointshop.item.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
			1L, "ITM-AA-002", "무지개", "https://thumbnail.url/image.jpg", BigDecimal.valueOf(450)
		);
		OrderPointItemCommand command = new OrderPointItemCommand(memberSnapshot, pointItemSnapshot);

		Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

		// Repository/Client mock
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(pointItemRepository.findById(anyLong())).willReturn(
			Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class))
		);
		given(pointClient.getTotalPoints(1L)).willReturn(BigDecimal.valueOf(500));
		given(timeUtils.now()).willReturn(LocalDateTime.now(fixedClock));
		given(plantGrowthItemRepository.save(any()))
			.willAnswer(invocation -> invocation.getArgument(0)); // 입력 그대로 반환

		// when
		OrderPointItemResponse response = pointItemOrderService.orderPointItem(command);

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
		OrderPointItemCommand command = mock(OrderPointItemCommand.class);
		when(command.memberSnapshot()).thenReturn(memberSnapshot);
		when(command.pointItemSnapshot()).thenReturn(pointItemSnapshot);

		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(pointItemRepository.findById(anyLong())).willReturn(
			Optional.of(mock(com.example.green.domain.pointshop.item.entity.PointItem.class))
		);
		given(pointClient.getTotalPoints(1L)).willReturn(BigDecimal.valueOf(50));

		assertThatThrownBy(() -> pointItemOrderService.orderPointItem(command))
			.isInstanceOf(PointItemException.class)
			.hasMessage(PointItemExceptionMessage.NOT_POSSIBLE_BUY_ITEM.getMessage());
	}

}
