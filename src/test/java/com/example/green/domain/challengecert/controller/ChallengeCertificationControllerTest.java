package com.example.green.domain.challengecert.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationDetailResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.enums.CertificationStatus;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;
import com.example.green.domain.challengecert.service.ChallengeCertificationService;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChallengeCertificationController.class)
class ChallengeCertificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ChallengeCertificationService challengeCertificationService;

	private static final String BASE_URL = "/api/challenges";
	private static final Long TEST_CHALLENGE_ID = 1L;
	private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";
	private static final String TEST_REVIEW = "오늘도 열심히 운동했습니다!";

	/**
	 * 인증된 사용자 설정을 위한 헬퍼 메서드
	 */
	private Authentication createAuthenticatedUser() {
		PrincipalDetails principalDetails = new PrincipalDetails(1L, "test@example.com", "ROLE_USER", "테스트사용자",
			"test@example.com");
		return new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());
	}

	@Test
	void 로그인_사용자가_챌린지_인증을_성공적으로_생성한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		ChallengeCertificationCreateResponseDto response = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(100L)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willReturn(response);

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("챌린지 인증이 성공적으로 생성되었습니다."))
			.andExpect(jsonPath("$.result.certificationId").value(100L));

		then(challengeCertificationService).should()
			.createCertification(eq(TEST_CHALLENGE_ID), any(), any());
	}

	@Test
	@WithAnonymousUser
	void 비로그인_사용자가_챌린지_인증_시도시_401_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void 필수_필드가_누락된_요청시_400_에러가_발생한다() throws Exception {
		// given - certificationDate가 누락된 요청
		String invalidRequest = """
			{
				"certificationImageUrl": "https://example.com/image.jpg",
				"certificationReview": "테스트 후기"
			}
			""";

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidRequest)
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void 인증_이미지_URL이_빈값인_경우_400_에러가_발생한다() throws Exception {
		// given
		String invalidRequest = """
			{
				"certificationDate": "2024-01-15",
				"certificationImageUrl": "",
				"certificationReview": "테스트 후기"
			}
			""";

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidRequest)
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void 후기가_45자를_초과하는_경우_400_에러가_발생한다() throws Exception {
		// given
		String longReview = "a".repeat(46); // 46자
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(longReview)
			.build();

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void 존재하지_않는_챌린지에_인증_시도시_404_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willThrow(new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND));

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND.getMessage()));
	}

	@Test
	void 존재하지_않는_회원이_인증_시도시_404_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willThrow(new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage()));
	}

	@Test
	void 참여하지_않은_챌린지에_인증_시도시_400_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willThrow(new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(ChallengeExceptionMessage.NOT_PARTICIPATING.getMessage()));
	}

	@Test
	void 중복_인증_시도시_400_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willThrow(new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS));

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(
				jsonPath("$.message").value(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS.getMessage()));
	}

	@Test
	void 미래_날짜로_인증_시도시_400_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willThrow(new ChallengeCertException(ChallengeCertExceptionMessage.INVALID_CERTIFICATION_DATE));

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(
				jsonPath("$.message").value(ChallengeCertExceptionMessage.INVALID_CERTIFICATION_DATE.getMessage()));
	}

	@Test
	void 후기_없이도_인증_생성이_성공한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(null) // 후기 없음
			.build();

		ChallengeCertificationCreateResponseDto response = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(101L)
			.build();

		given(challengeCertificationService.createCertification(eq(TEST_CHALLENGE_ID), any(), any()))
			.willReturn(response);

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("챌린지 인증이 성공적으로 생성되었습니다."))
			.andExpect(jsonPath("$.result.certificationId").value(101L));
	}

	@Test
	void JSON_형식이_잘못된_경우_400_에러가_발생한다() throws Exception {
		// given
		String malformedJson = "{ invalid json }";

		// when & then
		mockMvc.perform(post(BASE_URL + "/{challengeId}/certifications", TEST_CHALLENGE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(malformedJson)
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void challengeId가_문자열인_경우_400_에러가_발생한다() throws Exception {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		// when & then
		mockMvc.perform(post(BASE_URL + "/invalid/certifications")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isBadRequest());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	void 개인_챌린지_인증_목록을_성공적으로_조회한다() throws Exception {
		// given
		Long cursor = null;
		CursorTemplate<Long, ChallengeCertificationListResponseDto> response = CursorTemplate.ofWithNextCursor(
			10L,
			List.of(
				ChallengeCertificationListResponseDto.builder()
					.id(1L)
					.memberId(1L)
					.memberNickname("테스트사용자1")
					.memberEmail("test1@example.com")
					.certificationImageUrl("https://example.com/image1.jpg")
					.certificationReview("오늘도 열심히 운동했습니다!")
					.certifiedDate(LocalDate.of(2024, 1, 15))
					.status(CertificationStatus.PAID)
					.build(),
				ChallengeCertificationListResponseDto.builder()
					.id(2L)
					.memberId(2L)
					.memberNickname("테스트사용자2")
					.memberEmail("test2@example.com")
					.certificationImageUrl("https://example.com/image2.jpg")
					.certificationReview("홈트 완료!")
					.certifiedDate(LocalDate.of(2024, 1, 14))
					.status(CertificationStatus.PENDING)
					.build()
			)
		);

		given(challengeCertificationService.getPersonalChallengeCertifications(eq(cursor), any()))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/personal")
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("개인 챌린지 인증 목록이 성공적으로 조회되었습니다."))
			.andExpect(jsonPath("$.result.hasNext").value(true))
			.andExpect(jsonPath("$.result.nextCursor").value(10L))
			.andExpect(jsonPath("$.result.content").isArray())
			.andExpect(jsonPath("$.result.content[0].id").value(1L))
			.andExpect(jsonPath("$.result.content[0].memberNickname").value("테스트사용자1"))
			.andExpect(jsonPath("$.result.content[0].status").value("PAID"))
			.andExpect(jsonPath("$.result.content[1].id").value(2L))
			.andExpect(jsonPath("$.result.content[1].status").value("PENDING"));

		then(challengeCertificationService).should().getPersonalChallengeCertifications(eq(cursor), any());
	}

	@Test
	void 팀_챌린지_인증_목록을_성공적으로_조회한다() throws Exception {
		// given
		Long cursor = 5L;
		CursorTemplate<Long, ChallengeCertificationListResponseDto> response = CursorTemplate.of(
			List.of(
				ChallengeCertificationListResponseDto.builder()
					.id(3L)
					.memberId(3L)
					.memberNickname("테스트사용자3")
					.memberEmail("test3@example.com")
					.certificationImageUrl("https://example.com/image3.jpg")
					.certificationReview("팀 러닝 완료!")
					.certifiedDate(LocalDate.of(2024, 1, 15))
					.status(CertificationStatus.PAID)
					.build()
			)
		);

		given(challengeCertificationService.getTeamChallengeCertifications(eq(cursor), any()))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/team")
				.param("cursor", cursor.toString())
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("팀 챌린지 인증 목록이 성공적으로 조회되었습니다."))
			.andExpect(jsonPath("$.result.hasNext").value(false))
			.andExpect(jsonPath("$.result.nextCursor").doesNotExist())
			.andExpect(jsonPath("$.result.content").isArray())
			.andExpect(jsonPath("$.result.content[0].id").value(3L))
			.andExpect(jsonPath("$.result.content[0].memberNickname").value("테스트사용자3"))
			.andExpect(jsonPath("$.result.content[0].status").value("PAID"));

		then(challengeCertificationService).should().getTeamChallengeCertifications(eq(cursor), any());
	}

	@Test
	void 챌린지_인증_상세_정보를_성공적으로_조회한다() throws Exception {
		// given
		Long certificationId = 100L;
		ChallengeCertificationDetailResponseDto detailResponse = new ChallengeCertificationDetailResponseDto(
			certificationId,
			1L,
			"테스트사용자",
			"test@example.com",
			TEST_IMAGE_URL,
			TEST_REVIEW,
			LocalDateTime.of(2024, 1, 15, 14, 30),
			LocalDate.of(2024, 1, 15),
			CertificationStatus.PAID
		);

		given(challengeCertificationService.getChallengeCertificationDetail(eq(certificationId), any()))
			.willReturn(detailResponse);

		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/{certId}", certificationId)
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("챌린지 인증 상세 정보가 성공적으로 조회되었습니다."))
			.andExpect(jsonPath("$.result.id").value(certificationId))
			.andExpect(jsonPath("$.result.memberNickname").value("테스트사용자"))
			.andExpect(jsonPath("$.result.certificationImageUrl").value(TEST_IMAGE_URL))
			.andExpect(jsonPath("$.result.certificationReview").value(TEST_REVIEW))
			.andExpect(jsonPath("$.result.status").value("PAID"));

		then(challengeCertificationService).should().getChallengeCertificationDetail(eq(certificationId), any());
	}

	@Test
	void 존재하지_않는_인증_조회시_404_에러가_발생한다() throws Exception {
		// given
		Long nonExistentCertificationId = 999L;

		given(challengeCertificationService.getChallengeCertificationDetail(eq(nonExistentCertificationId), any()))
			.willThrow(new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/{certId}", nonExistentCertificationId)
				.with(csrf())
				.with(authentication(createAuthenticatedUser())))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("인증을 찾을 수 없습니다."));

		then(challengeCertificationService).should()
			.getChallengeCertificationDetail(eq(nonExistentCertificationId), any());
	}

	@Test
	@WithAnonymousUser
	void 인증되지_않은_사용자가_개인_인증_목록_조회시_403_에러가_발생한다() throws Exception {
		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/personal"))
			.andExpect(status().is3xxRedirection());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	@WithAnonymousUser
	void 인증되지_않은_사용자가_팀_인증_목록_조회시_403_에러가_발생한다() throws Exception {
		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/team"))
			.andExpect(status().is3xxRedirection());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}

	@Test
	@WithAnonymousUser
	void 인증되지_않은_사용자가_인증_상세_조회시_403_에러가_발생한다() throws Exception {
		// when & then
		mockMvc.perform(get("/api/my/challenges/certifications/123"))
			.andExpect(status().is3xxRedirection());

		then(challengeCertificationService).shouldHaveNoInteractions();
	}
}
