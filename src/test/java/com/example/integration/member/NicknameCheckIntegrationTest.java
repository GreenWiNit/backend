package com.example.integration.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.integration.common.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@DisplayName("닉네임 중복 확인 API 통합 테스트")
class NicknameCheckIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private org.springframework.test.web.servlet.MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("사용 가능한 닉네임 확인 - 전체 플로우")
	void checkNickname_AvailableNickname_IntegrationTest() throws Exception {
		// Given
		String availableNickname = "uniqueNickname";
		String requestBody = createNicknameCheckRequest(availableNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(availableNickname))
			.andExpect(jsonPath("$.available").value(true))
			.andExpect(jsonPath("$.message").value("사용 가능한 닉네임입니다."));
	}

	@Test
	@DisplayName("이미 사용 중인 닉네임 확인 - 전체 플로우")
	void checkNickname_TakenNickname_IntegrationTest() throws Exception {
		// Given
		String takenNickname = "existingNickname";
		Member existingMember = Member.create("google_123", "기존회원", "existing@example.com");
		existingMember.updateProfile(takenNickname, null);
		memberRepository.save(existingMember);

		String requestBody = createNicknameCheckRequest(takenNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(takenNickname))
			.andExpect(jsonPath("$.available").value(false))
			.andExpect(jsonPath("$.message").value("중복된 닉네임이 존재합니다."));
	}

	@Test
	@DisplayName("탈퇴한 회원의 닉네임은 사용 가능 - 전체 플로우")
	void checkNickname_WithdrawnMemberNickname_IntegrationTest() throws Exception {
		// Given
		String withdrawnNickname = "withdrawnNickname";
		Member withdrawnMember = Member.create("google_456", "탈퇴회원", "withdrawn@example.com");
		withdrawnMember.updateProfile(withdrawnNickname, null);
		withdrawnMember.withdraw();
		memberRepository.save(withdrawnMember);

		String requestBody = createNicknameCheckRequest(withdrawnNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(withdrawnNickname))
			.andExpect(jsonPath("$.available").value(true))
			.andExpect(jsonPath("$.message").value("사용 가능한 닉네임입니다."));
	}

	@Test
	@DisplayName("여러 회원 중 특정 닉네임 확인 - 전체 플로우")
	void checkNickname_MultipleMembers_IntegrationTest() throws Exception {
		// Given
		Member member1 = Member.create("google_1", "회원1", "member1@example.com");
		member1.updateProfile("nickname1", null);
		
		Member member2 = Member.create("google_2", "회원2", "member2@example.com");
		member2.updateProfile("nickname2", null);
		
		Member member3 = Member.create("google_3", "회원3", "member3@example.com");
		member3.updateProfile("nickname3", null);
		
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		// When & Then: 존재하는 닉네임
		String existingRequest = createNicknameCheckRequest("nickname2");
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(existingRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(false));

		// When & Then: 존재하지 않는 닉네임
		String nonExistingRequest = createNicknameCheckRequest("nickname4");
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(nonExistingRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	@DisplayName("Bean Validation 검증 - 빈 닉네임")
	void checkNickname_EmptyNickname_ValidationFails() throws Exception {
		// Given
		String requestBody = createNicknameCheckRequest("");

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("2자 미만 닉네임")
	void checkNickname_TooShortNickname_ValidationFails() throws Exception {
		// Given
		String requestBody = createNicknameCheckRequest("a");

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("20자 초과 닉네임")
	void checkNickname_TooLongNickname_ValidationFails() throws Exception {
		// Given
		String tooLongNickname = "a".repeat(21); // 21자
		String requestBody = createNicknameCheckRequest(tooLongNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("경계값 테스트 (2자, 20자)")
	void checkNickname_BoundaryValues_ValidationPasses() throws Exception {
		// Given: 최소 길이 (2자)
		String minLengthRequest = createNicknameCheckRequest("ab");
		
		// Given: 최대 길이 (20자)
		String maxLengthRequest = createNicknameCheckRequest("a".repeat(20));

		// When & Then: 최소 길이
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(minLengthRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));

		// When & Then: 최대 길이
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(maxLengthRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	@DisplayName("특수문자 포함 닉네임은 Bean Validation 오류")
	void checkNickname_SpecialCharacters_IntegrationTest() throws Exception {
		// Given
		String specialNickname = "nick@123!";
		String requestBody = createNicknameCheckRequest(specialNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("한글 닉네임 처리")
	void checkNickname_KoreanCharacters_IntegrationTest() throws Exception {
		// Given
		String koreanNickname = "한글닉네임";
		String requestBody = createNicknameCheckRequest(koreanNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(koreanNickname))
			.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	@DisplayName("공백 포함 닉네임은 오류")
	void checkNickname_WithSpaces_IntegrationTest() throws Exception {
		// Given
		String nicknameWithSpaces = "nick name";
		String requestBody = createNicknameCheckRequest(nicknameWithSpaces);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("대소문자 구분 확인")
	void checkNickname_CaseSensitive_IntegrationTest() throws Exception {
		// Given: 대문자 닉네임으로 회원 생성 (고유한 닉네임 사용)
		String upperCaseNickname = "aabb";
		Member member = Member.create("google_case_test", "대소문자회원", "casetest@example.com");
		member.updateProfile(upperCaseNickname, null);
		memberRepository.save(member);

		// When & Then: 소문자로 확인 (다른 닉네임으로 인식)
		String lowerCaseRequest = createNicknameCheckRequest("aaBB");
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(lowerCaseRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));

		// When & Then: 동일한 대문자로 확인 (중복으로 인식)
		String sameUpperCaseRequest = createNicknameCheckRequest(upperCaseNickname);
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sameUpperCaseRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	@DisplayName("유효한 문자만 포함된 닉네임 처리")
	void checkNickname_ValidCharactersOnly_IntegrationTest() throws Exception {
		// Given: 한글, 영문, 숫자만 포함된 닉네임
		String validNickname = "한글English123";
		String requestBody = createNicknameCheckRequest(validNickname);
		
		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(validNickname))
			.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	@DisplayName("이모지 포함 닉네임은 오류")
	void checkNickname_WithEmoji_IntegrationTest() throws Exception {
		// Given
		String emojiNickname = "닉네임😀";
		String requestBody = createNicknameCheckRequest(emojiNickname);

		// When & Then
		mockMvc.perform(post("/api/members/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());
	}

	private String createNicknameCheckRequest(String nickname) throws Exception {
		return objectMapper.writeValueAsString(new TestNicknameCheckRequest(nickname));
	}

	private record TestNicknameCheckRequest(String nickname) {}
} 