package com.example.integration.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.WithdrawReasonRepository;
import com.example.green.domain.member.service.WithdrawService;
import com.example.integration.common.BaseIntegrationTest;

class WithdrawMemberWithReasonIntegrationTest extends BaseIntegrationTest {

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
        withdrawReasonRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("탈퇴 사유와 함께 회원 탈퇴 시 전체 플로우가 성공적으로 처리된다")
    void withdrawMemberWithReason_WithValidRequest_ShouldSucceed() {
        // given
        String memberKey = testMember.getMemberKey();
        Long memberId = testMember.getId();
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            null  // OTHER 외에는 customReason 무시됨
        );

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        Member updatedMember = memberRepository.findById(memberId).orElseThrow();
        assertThat(updatedMember.getStatus()).isEqualTo(MemberStatus.DELETED);
        assertThat(updatedMember.isDeleted()).isTrue();
        assertThat(updatedMember.getLastLoginAt()).isNull();
        assertThat(updatedMember.isWithdrawn()).isTrue();

        assertThat(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
            .isEmpty();

        Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
        assertThat(withdrawReason).isPresent();
        assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.SERVICE_DISSATISFACTION);
        assertThat(withdrawReason.get().getCustomReason()).isNull(); // OTHER 외에는 customReason이 null로 처리됨
        assertThat(withdrawReason.get().getMemberKey()).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("기타 사유 선택 시 상세 사유와 함께 탈퇴 성공")
    void withdrawMemberWithReason_WithOtherReason_ShouldSucceed() {
        // given
        String memberKey = testMember.getMemberKey();
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.OTHER,
            "개인적인 사유로 인해 탈퇴합니다."
        );

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
        assertThat(withdrawReason).isPresent();
        assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.OTHER);
        assertThat(withdrawReason.get().getCustomReason()).isEqualTo("개인적인 사유로 인해 탈퇴합니다.");
        assertThat(withdrawReason.get().hasCustomReason()).isTrue();

        Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(updatedMember.isWithdrawn()).isTrue();
    }

    @Test
    @DisplayName("사용자 정의 사유 없이 일반 사유로 탈퇴 성공")
    void withdrawMemberWithReason_WithoutCustomReason_ShouldSucceed() {
        // given
        String memberKey = testMember.getMemberKey();
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.PRIVACY_CONCERN,
            null
        );

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
        assertThat(withdrawReason).isPresent();
        assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.PRIVACY_CONCERN);
        assertThat(withdrawReason.get().getCustomReason()).isNull();
        assertThat(withdrawReason.get().hasCustomReason()).isFalse();

        Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(updatedMember.isWithdrawn()).isTrue();
    }

    @Test
    @DisplayName("프로필 이미지가 있는 회원 탈퇴 시 파일 처리도 함께 수행된다")
    void withdrawMemberWithReason_WithProfileImage_ShouldProcessFile() {
        // given
        String memberKey = testMember.getMemberKey();
        String profileImageUrl = testMember.getProfile().getProfileImageUrl();
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.PRIVACY_PROTECTION,
            null
        );

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(fileService).unUseImage(profileImageUrl);

        Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(updatedMember.isWithdrawn()).isTrue();

        Optional<WithdrawReason> withdrawReason = withdrawReasonRepository.findByMemberKey(memberKey);
        assertThat(withdrawReason).isPresent();
        assertThat(withdrawReason.get().getReasonType()).isEqualTo(WithdrawReasonType.PRIVACY_PROTECTION);
    }
} 