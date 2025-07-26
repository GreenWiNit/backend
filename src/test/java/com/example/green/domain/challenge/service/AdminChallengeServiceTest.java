package com.example.green.domain.challenge.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challengecert.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;

@ExtendWith(MockitoExtension.class)
class AdminChallengeServiceTest {

	@Mock
	private PersonalChallengeRepository personalChallengeRepository;

	@Mock
	private TeamChallengeRepository teamChallengeRepository;

	@Mock
	private TeamChallengeGroupRepository teamChallengeGroupRepository;

	@Mock
	private PersonalChallengeParticipationRepository personalChallengeParticipationRepository;

	@Mock
	private TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;

	@InjectMocks
	private AdminChallengeService adminChallengeService;

	private PersonalChallenge mockPersonalChallenge;

	@Mock
	private TeamChallengeGroup mockTeamChallengeGroup;

	private LocalDateTime testTime;

	@BeforeEach
	void setUp() {
		testTime = LocalDateTime.now();

		mockPersonalChallenge = PersonalChallenge.create(
			"TEST-CHALLENGE-001",
			"테스트 개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(1000L),
			testTime.minusDays(1),
			testTime.plusDays(7),
			"test-image.jpg",
			"테스트 챌린지 내용",
			ChallengeDisplayStatus.VISIBLE
		);

		// TeamChallengeGroup mock 초기화는 실제 TeamChallengeGroup.create() 메서드가 필요하므로
		// 각 테스트에서 직접 생성하거나 mock() 사용
	}

	@Test
	void 챌린지를_생성할_수_있다() {
		// given
		AdminChallengeCreateRequestDto request = new AdminChallengeCreateRequestDto(
			"새로운 챌린지",
			2000,
			ChallengeType.PERSONAL,
			testTime.plusDays(1),
			testTime.plusDays(10),
			ChallengeDisplayStatus.VISIBLE,
			"https://example.com/challenge.jpg",
			"새로운 챌린지 내용",
			null
		);

		PersonalChallenge savedChallenge = PersonalChallenge.create(
			"ADMIN-123456789",
			request.challengeName(),
			ChallengeStatus.PROCEEDING,
			PointAmount.of(request.challengePoint().longValue()),
			request.beginDateTime(),
			request.endDateTime(),
			null,
			request.challengeContent(),
			request.displayStatus()
		);

		// Reflection을 사용하여 ID 설정 (테스트용)
		given(personalChallengeRepository.save(any(PersonalChallenge.class)))
			.willAnswer(invocation -> {
				PersonalChallenge challenge = invocation.getArgument(0);
				// 실제 환경에서는 ID를 자동 생성하지만, 테스트에서는 임의로 설정
				try {
					java.lang.reflect.Field idField = challenge.getClass().getSuperclass().getDeclaredField("id");
					idField.setAccessible(true);
					idField.set(challenge, 1L);
				} catch (Exception e) {
					// 테스트용이므로 예외 처리 생략
				}
				return challenge;
			});

		// when
		Long result = adminChallengeService.createChallenge(request);

		// then
		assertThat(result).isEqualTo(1L);
		verify(personalChallengeRepository).save(any(PersonalChallenge.class));
	}

	@Test
	void 챌린지를_수정할_수_있다() {
		// given
		Long challengeId = 1L;
		AdminChallengeUpdateRequestDto request = new AdminChallengeUpdateRequestDto(
			"수정된 챌린지",
			3000,
			testTime.plusDays(2),
			testTime.plusDays(12),
			"수정된 챌린지 내용",
			null
		);

		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.of(mockPersonalChallenge));

		// when
		assertThatCode(() -> adminChallengeService.updateChallenge(challengeId, request))
			.doesNotThrowAnyException();

		// then
		verify(personalChallengeRepository).findById(challengeId);
	}

	@Test
	void 존재하지_않는_챌린지_수정_시_예외가_발생한다() {
		// given
		Long challengeId = 999L;
		AdminChallengeUpdateRequestDto request = new AdminChallengeUpdateRequestDto(
			"수정된 챌린지",
			3000,
			testTime.plusDays(2),
			testTime.plusDays(12),
			"수정된 챌린지 내용",
			null
		);

		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.empty());
		lenient().when(teamChallengeRepository.findById(challengeId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminChallengeService.updateChallenge(challengeId, request))
			.isInstanceOf(ChallengeException.class)
			.hasMessageContaining("관리자 - 챌린지를 찾을 수 없습니다");
	}

	@Test
	void 챌린지_이미지를_수정할_수_있다() {
		// given
		Long challengeId = 1L;
		AdminChallengeImageUpdateRequestDto request = new AdminChallengeImageUpdateRequestDto("new-image.jpg");

		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.of(mockPersonalChallenge));

		// when
		AdminChallengeDetailResponseDto result = adminChallengeService.updateChallengeImage(challengeId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.challengeImage()).isEqualTo("new-image.jpg");
		verify(personalChallengeRepository).findById(challengeId);
	}

	@Test
	void 챌린지_전시_상태를_수정할_수_있다() {
		// given
		Long challengeId = 1L;
		AdminChallengeDisplayStatusUpdateRequestDto request = new AdminChallengeDisplayStatusUpdateRequestDto(
			ChallengeDisplayStatus.HIDDEN
		);

		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.of(mockPersonalChallenge));

		// when
		assertThatCode(() -> adminChallengeService.updateChallengeDisplayStatus(challengeId, request))
			.doesNotThrowAnyException();

		// then
		verify(personalChallengeRepository).findById(challengeId);
	}

	@Test
	void 개인_챌린지_목록을_조회할_수_있다() {
		// given
		Long cursor = 10L;
		CursorTemplate mockResult = CursorTemplate.ofEmpty();

		given(personalChallengeRepository.findAllForAdminByCursor(cursor, 10))
			.willReturn(mockResult);

		// when
		CursorTemplate result = adminChallengeService.getPersonalChallenges(cursor);

		// then
		assertThat(result).isNotNull();
		verify(personalChallengeRepository).findAllForAdminByCursor(cursor, 10);
	}

	@Test
	void 팀_챌린지_목록을_조회할_수_있다() {
		// given
		Long cursor = null;
		CursorTemplate mockResult = CursorTemplate.ofEmpty();

		given(teamChallengeRepository.findAllForAdminByCursor(cursor, 10))
			.willReturn(mockResult);

		// when
		CursorTemplate result = adminChallengeService.getTeamChallenges(cursor);

		// then
		assertThat(result).isNotNull();
		verify(teamChallengeRepository).findAllForAdminByCursor(cursor, 10);
	}

	@Test
	void 챌린지_상세_정보를_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.of(mockPersonalChallenge));

		// when
		AdminChallengeDetailResponseDto result = adminChallengeService.getChallengeDetail(challengeId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.challengeName()).isEqualTo("테스트 개인 챌린지");
		verify(personalChallengeRepository).findById(challengeId);
	}

	@Test
	void 존재하지_않는_챌린지_조회_시_예외가_발생한다() {
		// given
		Long challengeId = 999L;
		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.empty());
		lenient().when(teamChallengeRepository.findById(challengeId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminChallengeService.getChallengeDetail(challengeId))
			.isInstanceOf(ChallengeException.class)
			.hasMessageContaining("관리자 - 챌린지를 찾을 수 없습니다");
	}

	@Test
	void 챌린지_참가자_목록을_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		Long cursor = 5L;
		CursorTemplate mockResult = CursorTemplate.ofEmpty();

		given(personalChallengeRepository.findById(challengeId))
			.willReturn(Optional.of(mockPersonalChallenge));
		given(personalChallengeParticipationRepository.findParticipantsByChallengeIdCursor(challengeId, cursor, 10))
			.willReturn(mockResult);

		// when
		CursorTemplate result = adminChallengeService.getChallengeParticipants(challengeId, cursor);

		// then
		assertThat(result).isNotNull();
		verify(personalChallengeParticipationRepository).findParticipantsByChallengeIdCursor(challengeId, cursor, 10);
	}

	@Test
	void 그룹_목록을_조회할_수_있다() {
		// given
		Long cursor = null;
		CursorTemplate mockResult = CursorTemplate.ofEmpty();

		given(teamChallengeGroupRepository.findAllForAdminByCursor(cursor, 10))
			.willReturn(mockResult);

		// when
		CursorTemplate result = adminChallengeService.getGroups(cursor);

		// then
		assertThat(result).isNotNull();
		verify(teamChallengeGroupRepository).findAllForAdminByCursor(cursor, 10);
	}

	@Test
	void 그룹_상세_정보를_조회할_수_있다() {
		// given
		Long groupId = 1L;

		// mockTeamChallengeGroup의 필요한 메서드들을 stub
		lenient().when(mockTeamChallengeGroup.getTeamCode()).thenReturn("T-20250109-143523-C8NQ");
		lenient().when(mockTeamChallengeGroup.getGroupName()).thenReturn("테스트 팀");
		lenient().when(mockTeamChallengeGroup.getMaxParticipants()).thenReturn(10);
		lenient().when(mockTeamChallengeGroup.getCurrentParticipants()).thenReturn(5);
		lenient().when(mockTeamChallengeGroup.getGroupBeginDateTime()).thenReturn(testTime);
		lenient().when(mockTeamChallengeGroup.getGroupEndDateTime()).thenReturn(testTime.plusDays(7));
		lenient().when(mockTeamChallengeGroup.getGroupAddress()).thenReturn(null);
		lenient().when(mockTeamChallengeGroup.getCreatedDate()).thenReturn(testTime);

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(mockTeamChallengeGroup));
		given(teamChallengeGroupParticipationRepository.findByTeamChallengeGroup(mockTeamChallengeGroup))
			.willReturn(Arrays.asList()); // 빈 참가자 목록

		// when
		AdminTeamChallengeGroupDetailResponseDto result = adminChallengeService.getGroupDetail(groupId);

		// then
		assertThat(result).isNotNull();
		verify(teamChallengeGroupRepository).findById(groupId);
		verify(teamChallengeGroupParticipationRepository).findByTeamChallengeGroup(mockTeamChallengeGroup);
	}

	@Test
	void 존재하지_않는_그룹_조회_시_예외가_발생한다() {
		// given
		Long groupId = 999L;
		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminChallengeService.getGroupDetail(groupId))
			.isInstanceOf(ChallengeException.class)
			.hasMessageContaining("관리자 - 팀 챌린지 그룹을 찾을 수 없습니다");
	}
} 