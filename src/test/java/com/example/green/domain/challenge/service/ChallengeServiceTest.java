package com.example.green.domain.challenge.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.repository.ChallengeRepository;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.infra.client.FileClient;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

	@Mock
	private ChallengeRepository challengeRepository;
	@Mock
	private FileClient fileClient;
	@Mock
	private Clock clock;
	@Mock
	private SequenceService sequenceService;
	@InjectMocks
	private ChallengeService challengeService;

	private Challenge mockChallenge;

	@BeforeEach
	void setUp() {
		mockChallenge = mock(Challenge.class);
		;
	}

	@Test
	void 챌린지_생성() {
		// given
		AdminChallengeCreateDto dto = mock(AdminChallengeCreateDto.class);
		String imageUrl = "url";
		챌린지_생성_스텁(dto, imageUrl);

		// when
		Long result = challengeService.create(dto, ChallengeType.PERSONAL);

		// then
		assertThat(result).isEqualTo(1L);
		verify(fileClient).confirmUsingImage(imageUrl);
	}

	@Test
	void 챌린지_조회_후_참여() {
		// given
		when(challengeRepository.getById(anyLong())).thenReturn(mockChallenge);

		// when
		challengeService.join(1L, 1L);

		// then
		verify(mockChallenge).participate(1L);
	}

	@Test
	void 챌린지_조회_후_전시() {
		// given
		when(challengeRepository.getById(anyLong())).thenReturn(mockChallenge);

		// when
		challengeService.show(1L);

		// then
		verify(mockChallenge).show();
	}

	@Test
	void 챌린지_조회_후_미전시() {
		// given
		when(challengeRepository.getById(anyLong())).thenReturn(mockChallenge);

		// when
		challengeService.hide(1L);

		// then
		verify(mockChallenge).hide();
	}

	@Test
	void 챌린지_조회_후_수정() {
		// given
		AdminChallengeUpdateDto dto = mock(AdminChallengeUpdateDto.class);
		챌린지_수정_스텁(dto);

		// when
		challengeService.update(1L, dto);

		// then
		verify(mockChallenge).updateInfo(any(ChallengeInfo.class));
		verify(mockChallenge).updateContent(any(ChallengeContent.class));
		verify(fileClient).swapImage(eq("before"), eq("after"));
	}

	private void 챌린지_수정_스텁(AdminChallengeUpdateDto dto) {
		when(challengeRepository.getById(anyLong())).thenReturn(mockChallenge);
		when(mockChallenge.getImageUrl()).thenReturn("before").thenReturn("after");
		when(dto.info()).thenReturn(mock(ChallengeInfo.class));
		ChallengeContent content = mock(ChallengeContent.class);
		when(dto.content()).thenReturn(content);
	}

	private void 챌린지_생성_스텁(AdminChallengeCreateDto dto, String imageUrl) {
		Challenge created = mock(Challenge.class);
		Challenge saved = mock(Challenge.class);
		String code = "CH-P-212121";
		when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
		when(clock.instant()).thenReturn(Instant.ofEpochSecond(1L));
		when(sequenceService.generateCode(any(SequenceType.class), any(LocalDateTime.class))).thenReturn(code);
		when(dto.toChallenge(eq(code), any(ChallengeType.class))).thenReturn(created);
		when(challengeRepository.save(eq(created))).thenReturn(saved);
		when(saved.getId()).thenReturn(1L);
		when(saved.getImageUrl()).thenReturn(imageUrl);
	}
}