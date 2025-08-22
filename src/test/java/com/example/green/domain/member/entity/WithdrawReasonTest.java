package com.example.green.domain.member.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.green.domain.member.entity.enums.WithdrawReasonType;

class WithdrawReasonTest {

    @Test
    @DisplayName("탈퇴 사유를 생성하면 회원 정보와 사유가 올바르게 설정된다")
    void create_ShouldSetMemberAndReason() {
        // given
        Member member = Member.create("google 123456", "테스트회원", "test@example.com", "테스트닉네임");
        WithdrawReasonType reasonType = WithdrawReasonType.SERVICE_DISSATISFACTION;
        String customReason = "서비스가 불편해서 탈퇴합니다.";

        // when
        WithdrawReason withdrawReason = WithdrawReason.create(member, reasonType, customReason);

        // then
        assertThat(withdrawReason.getMember()).isEqualTo(member);
        assertThat(withdrawReason.getMemberKey()).isEqualTo(member.getMemberKey());
        assertThat(withdrawReason.getReasonType()).isEqualTo(reasonType);
        assertThat(withdrawReason.getCustomReason()).isEqualTo(customReason);
    }

    @Test
    @DisplayName("사용자 정의 사유가 있으면 hasCustomReason이 true를 반환한다")
    void hasCustomReason_WithCustomReason_ReturnsTrue() {
        // given
        Member member = Member.create("google 123456", "테스트회원", "test@example.com", "테스트닉네임");
        WithdrawReason withdrawReason = WithdrawReason.create(
            member, 
            WithdrawReasonType.OTHER, 
            "개인적인 사유로 탈퇴합니다."
        );

        // when & then
        assertThat(withdrawReason.hasCustomReason()).isTrue();
    }

    @Test
    @DisplayName("사용자 정의 사유가 null이면 hasCustomReason이 false를 반환한다")
    void hasCustomReason_WithNullCustomReason_ReturnsFalse() {
        // given
        Member member = Member.create("google 123456", "테스트회원", "test@example.com", "테스트닉네임");
        WithdrawReason withdrawReason = WithdrawReason.create(
            member, 
            WithdrawReasonType.PRIVACY_CONCERN, 
            null
        );

        // when & then
        assertThat(withdrawReason.hasCustomReason()).isFalse();
    }

} 