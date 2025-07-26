package com.example.green.domain.challenge.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.service.AdminChallengeService;
import com.example.green.global.api.page.CursorTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminChallengeController.class)
class AdminChallengeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AdminChallengeService adminChallengeService;

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지를_생성할_수_있다() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		AdminChallengeCreateRequestDto request = new AdminChallengeCreateRequestDto(
			"테스트 챌린지",
			1000,
			ChallengeType.PERSONAL,
			now.plusDays(1),
			now.plusDays(7),
			ChallengeDisplayStatus.VISIBLE,
			"https://example.com/test-image.jpg",
			"테스트 챌린지 내용",
			null
		);
		Long expectedId = 1L;

		given(adminChallengeService.createChallenge(any(AdminChallengeCreateRequestDto.class)))
			.willReturn(expectedId);

		// when & then
		mockMvc.perform(post("/api/admin/challenges")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result").value(expectedId));

		verify(adminChallengeService).createChallenge(any(AdminChallengeCreateRequestDto.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지_생성_시_필수값이_없으면_400_오류가_발생한다() throws Exception {
		// given
		AdminChallengeCreateRequestDto invalidRequest = new AdminChallengeCreateRequestDto(
			"", // 빈 이름
			1000,
			ChallengeType.PERSONAL,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(7),
			ChallengeDisplayStatus.VISIBLE,
			null,
			"테스트 챌린지 내용",
			null
		);

		// when & then
		mockMvc.perform(post("/api/admin/challenges")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지를_수정할_수_있다() throws Exception {
		// given
		Long challengeId = 1L;
		LocalDateTime now = LocalDateTime.now();
		AdminChallengeUpdateRequestDto request = new AdminChallengeUpdateRequestDto(
			"수정된 챌린지",
			2000,
			now.plusDays(2),
			now.plusDays(8),
			"수정된 챌린지 내용",
			null
		);

		willDoNothing().given(adminChallengeService)
			.updateChallenge(eq(challengeId), any(AdminChallengeUpdateRequestDto.class));

		// when & then
		mockMvc.perform(put("/api/admin/challenges/{challengeId}", challengeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).updateChallenge(eq(challengeId), any(AdminChallengeUpdateRequestDto.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지_이미지를_수정할_수_있다() throws Exception {
		// given
		Long challengeId = 1L;
		AdminChallengeImageUpdateRequestDto request = new AdminChallengeImageUpdateRequestDto("new-image.jpg");

		AdminChallengeDetailResponseDto mockResponse = new AdminChallengeDetailResponseDto(
			challengeId,
			"TEST-001",
			"테스트 챌린지",
			ChallengeStatus.PROCEEDING,
			ChallengeType.PERSONAL,
			1000,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(7),
			ChallengeDisplayStatus.VISIBLE,
			"new-image.jpg",
			"테스트 내용",
			LocalDateTime.now()
		);

		given(
			adminChallengeService.updateChallengeImage(eq(challengeId), any(AdminChallengeImageUpdateRequestDto.class)))
			.willReturn(mockResponse);

		// when & then
		mockMvc.perform(patch("/api/admin/challenges/{challengeId}/image", challengeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.challengeImage").value("new-image.jpg"));

		verify(adminChallengeService).updateChallengeImage(eq(challengeId),
			any(AdminChallengeImageUpdateRequestDto.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지_전시_상태를_수정할_수_있다() throws Exception {
		// given
		Long challengeId = 1L;
		AdminChallengeDisplayStatusUpdateRequestDto request = new AdminChallengeDisplayStatusUpdateRequestDto(
			ChallengeDisplayStatus.HIDDEN
		);

		willDoNothing().given(adminChallengeService)
			.updateChallengeDisplayStatus(eq(challengeId), any(AdminChallengeDisplayStatusUpdateRequestDto.class));

		// when & then
		mockMvc.perform(patch("/api/admin/challenges/{challengeId}/visibility", challengeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).updateChallengeDisplayStatus(eq(challengeId),
			any(AdminChallengeDisplayStatusUpdateRequestDto.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 개인_챌린지_목록을_조회할_수_있다() throws Exception {
		// given
		CursorTemplate mockResult = CursorTemplate.ofEmpty();
		given(adminChallengeService.getPersonalChallenges(any())).willReturn(mockResult);

		// when & then
		mockMvc.perform(get("/api/admin/challenges/personal")
				.param("cursor", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).getPersonalChallenges(10L);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 팀_챌린지_목록을_조회할_수_있다() throws Exception {
		// given
		CursorTemplate mockResult = CursorTemplate.ofEmpty();
		given(adminChallengeService.getTeamChallenges(any())).willReturn(mockResult);

		// when & then
		mockMvc.perform(get("/api/admin/challenges/team"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).getTeamChallenges(null);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지_상세_정보를_조회할_수_있다() throws Exception {
		// given
		Long challengeId = 1L;
		AdminChallengeDetailResponseDto mockResponse = new AdminChallengeDetailResponseDto(
			challengeId,
			"TEST-001",
			"테스트 챌린지",
			ChallengeStatus.PROCEEDING,
			ChallengeType.PERSONAL,
			1000,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(7),
			ChallengeDisplayStatus.VISIBLE,
			null,
			"테스트 내용",
			LocalDateTime.now()
		);

		given(adminChallengeService.getChallengeDetail(challengeId)).willReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/api/admin/challenges/{challengeId}", challengeId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.id").value(challengeId));

		verify(adminChallengeService).getChallengeDetail(challengeId);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 챌린지_참가자_목록을_조회할_수_있다() throws Exception {
		// given
		Long challengeId = 1L;
		CursorTemplate mockResult = CursorTemplate.ofEmpty();
		given(adminChallengeService.getChallengeParticipants(eq(challengeId), any())).willReturn(mockResult);

		// when & then
		mockMvc.perform(get("/api/admin/challenges/{challengeId}/participants", challengeId)
				.param("cursor", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).getChallengeParticipants(challengeId, 5L);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 그룹_목록을_조회할_수_있다() throws Exception {
		// given
		CursorTemplate mockResult = CursorTemplate.ofEmpty();
		given(adminChallengeService.getGroups(any())).willReturn(mockResult);

		// when & then
		mockMvc.perform(get("/api/admin/challenges/groups"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(adminChallengeService).getGroups(null);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 그룹_상세_정보를_조회할_수_있다() throws Exception {
		// given
		Long groupId = 1L;
		AdminTeamChallengeGroupDetailResponseDto mockDto
			= new AdminTeamChallengeGroupDetailResponseDto(
			"T-20250109-143523-C8NQ",
			"google_4523",
			"google_2349, naver_1938",
			"함께 플로길 해요~",
			LocalDate.of(2025, 6, 8),
			LocalTime.of(20, 0),
			LocalTime.of(21, 0),
			"서울시 종로구 00강 입구",
			"1시간 동안 함께; 플로길 하는 코스입니다.",
			"https://open.kakao.com/o/sAczYWth"
		);

		given(adminChallengeService.getGroupDetail(groupId))
			.willReturn(mockDto); // 실제 응답 객체 생성 생략

		// when & then
		mockMvc.perform(get("/api/admin/challenges/groups/{groupId}", groupId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.teamCode").value("T-20250109-143523-C8NQ"));

		verify(adminChallengeService).getGroupDetail(groupId);
	}
}
