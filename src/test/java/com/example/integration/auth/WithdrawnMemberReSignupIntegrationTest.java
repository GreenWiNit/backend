package com.example.integration.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.CustomOAuth2UserService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.service.MemberService;
import com.example.integration.common.BaseIntegrationTest;

@DisplayName("탈퇴한 사용자 재가입 플로우 테스트")
class WithdrawnMemberReSignupIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private AuthService authService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	private MemberRepository memberRepository;

	private Member withdrawnMember;
	private String memberKey;
	private String originalNickname;
	private String newNickname;
	private String newProfileImageUrl;

	@BeforeEach
	void setUp() {
		// Given: 기존 회원 생성 및 탈퇴 처리
		memberKey = "google 123456789";
		originalNickname = "원래닉네임";
		newNickname = "새로운닉네임";
		newProfileImageUrl = "https://example.com/new-profile.jpg";

		// 1. 기존 회원 생성
		String originalMemberKey = memberService.signupFromOAuth2(
			"google", 
			"123456789", 
			"홍길동", 
			"test@example.com",
			originalNickname,
			"https://example.com/old-profile.jpg"
		);

		withdrawnMember = memberRepository.findByMemberKey(originalMemberKey).orElseThrow();

		// 2. 회원 탈퇴 처리
		withdrawnMember.withdraw();
		memberRepository.save(withdrawnMember);

		// 탈퇴 상태 확인
		assertThat(withdrawnMember.isWithdrawn()).isTrue();
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.DELETED);
	}

	@Test
	@DisplayName("탈퇴한 사용자는 신규 사용자로 인식되어야 한다")
	void shouldRecognizeWithdrawnUserAsNewUser() {
		// When: 활성 사용자 존재 여부 확인
		boolean isExistingUser = memberService.existsActiveByMemberKey(memberKey);

		// Then: 탈퇴한 사용자는 활성 사용자가 아니므로 false
		assertThat(isExistingUser).isFalse();
	}

	@Test
	@DisplayName("탈퇴한 사용자 재가입 시 새 정보로 복원되어야 한다")
	@Transactional
	void shouldRestoreWithdrawnMemberWithNewInfo() {
		// When: 재가입 처리
		TempTokenInfoDto tempInfo = TempTokenInfoDto.builder()
			.provider("google")
			.providerId("123456789")
			.name("홍길동")
			.email("test@example.com")
			.build();

		String resultMemberKey = authService.signup(tempInfo, newNickname, newProfileImageUrl);

		// Then: 동일한 memberKey 반환
		assertThat(resultMemberKey).isEqualTo(memberKey);

		// 회원 상태 및 정보 확인
		Member restoredMember = memberRepository.findByMemberKey(memberKey).orElseThrow();
		
		assertThat(restoredMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(restoredMember.isWithdrawn()).isFalse();
		assertThat(restoredMember.getProfile().getNickname()).isEqualTo(newNickname);
		assertThat(restoredMember.getProfile().getProfileImageUrl()).isEqualTo(newProfileImageUrl);
	}

	@Test
	@DisplayName("복원된 사용자로 토큰 생성이 성공해야 한다")
	@Transactional
	void shouldCreateTokenAfterRestore() {
		// Given: 재가입 처리
		TempTokenInfoDto tempInfo = TempTokenInfoDto.builder()
			.provider("google")
			.providerId("123456789")
			.name("홍길동")
			.email("test@example.com")
			.build();

		authService.signup(tempInfo, newNickname, newProfileImageUrl);

		// When: 토큰 생성 시도
		String refreshToken = tokenService.createRefreshToken(memberKey, "Web Browser", "127.0.0.1");
		String accessToken = tokenService.createAccessToken(memberKey, "ROLE_USER");

		// Then: 토큰 생성 성공
		assertThat(refreshToken).isNotBlank();
		assertThat(accessToken).isNotBlank();

		// 토큰 검증
		boolean isValidRefresh = tokenService.validateRefreshToken(refreshToken);
		boolean isValidAccess = tokenService.validateAccessToken(accessToken);

		assertThat(isValidRefresh).isTrue();
		assertThat(isValidAccess).isTrue();
	}

	@Test
	@DisplayName("전체 플로우: 탈퇴 -> OAuth2 인식 -> 재가입 -> 토큰 생성")
	@Transactional
	void shouldCompleteFullReSignupFlow() {
		// 1. OAuth2 신규 사용자 인식 확인
		boolean isExistingUser = memberService.existsActiveByMemberKey(memberKey);
		assertThat(isExistingUser).isFalse();

		// 2. 재가입 처리
		TempTokenInfoDto tempInfo = TempTokenInfoDto.builder()
			.provider("google")
			.providerId("123456789")
			.name("홍길동 Updated")  // OAuth2에서 받은 최신 정보
			.email("test@example.com")
			.build();

		String resultMemberKey = authService.signup(tempInfo, newNickname, newProfileImageUrl);

		// 3. 복원 확인
		Member restoredMember = memberRepository.findByMemberKey(memberKey).orElseThrow();
		assertThat(restoredMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(restoredMember.getName()).isEqualTo("홍길동 Updated");
		assertThat(restoredMember.getProfile().getNickname()).isEqualTo(newNickname);

		// 4. 토큰 생성 성공
		String refreshToken = tokenService.createRefreshToken(memberKey, "Web Browser", "127.0.0.1");
		assertThat(refreshToken).isNotBlank();

		// 5. 최종 상태 확인: 완전히 활성 상태
		boolean isFinallyActive = memberService.existsActiveByMemberKey(memberKey);
		assertThat(isFinallyActive).isTrue();
	}

	@Test
	@DisplayName("TokenService에서도 백업 복원이 정상 작동해야 한다")
	@Transactional
	void shouldBackupRestoreWorkInTokenService() {

		String refreshToken = tokenService.createRefreshToken(memberKey, "Web Browser", "127.0.0.1");

		assertThat(refreshToken).isNotBlank();

		Member restoredMember = memberRepository.findByMemberKey(memberKey).orElseThrow();
		assertThat(restoredMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(restoredMember.isWithdrawn()).isFalse();
	}

	@Test
	@DisplayName("이중 복원 호출시 멱등성이 보장되어야 한다")
	@Transactional
	void shouldEnsureIdempotencyOnDoubleRestore() {
		// Given
		TempTokenInfoDto tempInfo = TempTokenInfoDto.builder()
			.provider("google")
			.providerId("123456789")
			.name("홍길동")
			.email("test@example.com")
			.build();

		authService.signup(tempInfo, newNickname, newProfileImageUrl);
		
		Member firstRestore = memberRepository.findByMemberKey(memberKey).orElseThrow();
		String firstNickname = firstRestore.getProfile().getNickname();

		// When
		tokenService.createRefreshToken(memberKey, "Web Browser", "127.0.0.1");

		// Then
		Member secondRestore = memberRepository.findByMemberKey(memberKey).orElseThrow();
		assertThat(secondRestore.getProfile().getNickname()).isEqualTo(firstNickname);
		assertThat(secondRestore.getStatus()).isEqualTo(MemberStatus.NORMAL);
	}
} 