package com.example.green.domain.member.entity;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.enums.WithdrawReasonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawReason extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_reason_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 50)
    private WithdrawReasonType reasonType;

    @Column(name = "custom_reason", columnDefinition = "TEXT")
    private String customReason;

    @Column(name = "member_key", nullable = false, length = 100)
    private String memberKey;

    @Builder
    private WithdrawReason(Member member, WithdrawReasonType reasonType, String customReason) {
        this.member = member;
        this.memberKey = member.getMemberKey();
        this.reasonType = reasonType;
        this.customReason = customReason;
    }


    public static WithdrawReason create(Member member, WithdrawReasonType reasonType, String customReason) {
        return WithdrawReason.builder()
            .member(member)
            .reasonType(reasonType)
            .customReason(customReason)
            .build();
    }


    public boolean hasCustomReason() {
        return customReason != null && !customReason.trim().isEmpty();
    }
} 