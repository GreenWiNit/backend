package com.example.integration.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.auth.entity.TokenManager;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.file.service.FileService;
import com.example.green.domain.member.dto.WithdrawRequestDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.WithdrawReason;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.entity.enums.WithdrawReasonType;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.WithdrawReasonRepository;
import com.example.green.domain.member.service.WithdrawService;
import com.example.green.global.error.exception.BusinessException;
import com.example.integration.common.BaseIntegrationTest;

@DisplayName("회원 탈퇴 통합 테스트 - 새로운 플로우")
class WithdrawMemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private WithdrawService withdrawService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private WithdrawReasonRepository withdrawReasonRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@MockitoBean
	private FileService fileService;

	private Member testMember;

	@BeforeEach
	void setUp() {
		testMember = Member.create("google 123456", "테스트회원", "test@example.com", "테스트닉네임");
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
		withdrawReasonRepository.deleteAllInBatch();
		refreshTokenRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("탈퇴 사유와 함께 회원 탈퇴 시 전체 플로우가 성공적으로 처리된다")
	void withdrawMemberWithReason_WithValidMember_ShouldSucceed() {
		// given
		String memberKey = testMember.getMemberKey();
		Long memberId = testMember.getId();
		List<WithdrawReasonType> reasonTypes = Arrays.asList(
			WithdrawReasonType.SERVICE_DISSATISFACTION
		);
		WithdrawRequestDto request = new WithdrawRequestDto(
			reasonTypes,
			null
		);

		// when
		withdrawService.withdrawMemberWithReason(memberKey, request);

		// then
		Member updatedMember = memberRepository.findById(memberId).orElseThrow();
		assertThat(updatedMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(updatedMember.isDeleted()).isTrue();
		assertThat(updatedMember.getLastLoginAt()).isNull();
		assertThat(updatedMember.isWithdrawn()).isTrue();

		// then
		assertThat(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.isEmpty();

		assertThat(memberRepository.findActiveByMemberKey(memberKey))
			.isEmpty();

		// then
		Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
		assertThat(withdrawReason).isPresent();
		assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.SERVICE_DISSATISFACTION);
		assertThat(withdrawReason.get().getCustomReason()).isNull(); // OTHER 외에는 customReason이 null로 처리됨
	}

	@Test
	@DisplayName("존재하지 않는 회원 탈퇴 시 예외 발생")
	void withdrawMemberWithReason_WithNonExistentMember_ShouldThrowException() {
		// given
		String nonExistentMemberKey = "nonexistent 123456";
		List<WithdrawReasonType> reasonTypes = Arrays.asList(
			WithdrawReasonType.PRIVACY_CONCERN
		);
		WithdrawRequestDto request = new WithdrawRequestDto(
			reasonTypes,
			null
		);

		// when & then
		assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(nonExistentMemberKey, request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("프로필 이미지가 있는 회원 탈퇴 시 파일 처리도 함께 수행된다")
	void withdrawMemberWithReason_WithProfileImage_ShouldProcessFile() {
		// given
		String memberKey = testMember.getMemberKey();
		String profileImageUrl = testMember.getProfile().getProfileImageUrl();
		List<WithdrawReasonType> reasonTypes = Arrays.asList(
			WithdrawReasonType.PRIVACY_PROTECTION
		);
		WithdrawRequestDto request = new WithdrawRequestDto(
			reasonTypes,
			null
		);

		// when
		withdrawService.withdrawMemberWithReason(memberKey, request);

		// then
		verify(fileService).unUseImage(profileImageUrl);

		// then
		Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
		assertThat(updatedMember.isWithdrawn()).isTrue();

		// then
		Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
		assertThat(withdrawReason).isPresent();
        assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.PRIVACY_PROTECTION);
		assertThat(withdrawReason.get().getCustomReason()).isNull();
	}

	@Test
	@DisplayName("기타 사유 선택 시 상세 사유 없으면 예외 발생")
	void withdrawMemberWithReason_OtherReasonWithoutCustomReason_ShouldThrowException() {
		// given
		String memberKey = testMember.getMemberKey();
		List<WithdrawReasonType> reasonTypes = Arrays.asList(
			WithdrawReasonType.OTHER
		);
		WithdrawRequestDto request = new WithdrawRequestDto(
			reasonTypes,
			null // 기타 사유인데 상세 사유 없음
		);

		// when & then
		assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.WITHDRAW_CUSTOM_REASON_REQUIRED.getMessage());
	}
}