package com.example.integration.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.auth.entity.TokenManager;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.file.service.FileService;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.error.exception.BusinessException;
import com.example.integration.common.BaseIntegrationTest;

class WithdrawMemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@MockitoBean
	private FileService fileService;

	private Member testMember;

	@BeforeEach
	void setUp() {
		testMember = Member.create("google 123456", "테스트회원", "test@example.com");
		testMember.updateProfile("테스트닉네임", "https://example.com/profile.jpg");
		testMember = memberRepository.save(testMember);

		TokenManager tokenManager = TokenManager.create(
			"test-token-value",
			LocalDateTime.now().plusDays(7),
			testMember,
			"test-device-id",
			"192.168.1.1"
		);
		refreshTokenRepository.save(tokenManager);
	}

	@AfterEach
	void tearDown() {
		refreshTokenRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("정상 회원 탈퇴 시 전체 플로우가 성공적으로 처리된다")
	void withdrawMember_WithValidMember_ShouldSucceed() {
		// given
		String username = testMember.getUsername();
		Long memberId = testMember.getId();

		// when
		authService.withdrawMember(username);

		// then
		Member updatedMember = memberRepository.findById(memberId).orElseThrow();
		assertThat(updatedMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(updatedMember.isDeleted()).isTrue();
		assertThat(updatedMember.getLastLoginAt()).isNull();
		assertThat(updatedMember.isWithdrawn()).isTrue();

		assertThat(refreshTokenRepository.findAllByUsernameAndNotRevoked(username))
			.isEmpty();

		assertThat(memberRepository.findActiveByUsername(username))
			.isEmpty();
	}

	@Test
	@DisplayName("존재하지 않는 회원 탈퇴 시 예외 발생")
	void withdrawMember_WithNonExistentMember_ShouldThrowException() {
		// given
		String nonExistentUsername = "nonexistent";

		// when & then
		assertThatThrownBy(() -> authService.withdrawMember(nonExistentUsername))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("프로필 이미지가 있는 회원 탈퇴 시 파일 처리도 함께 수행된다")
	void withdrawMember_WithProfileImage_ShouldProcessFile() {
		// given
		String username = testMember.getUsername();
		String profileImageUrl = testMember.getProfile().getProfileImageUrl();

		// when
		authService.withdrawMember(username);

		// then
		verify(fileService).unUseImage(profileImageUrl);

		Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
		assertThat(updatedMember.isWithdrawn()).isTrue();
	}
}