package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseChallengeParticipation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime participatedAt;

    // 하위 클래스를 위한 protected 생성자
    protected BaseChallengeParticipation(
        Member member,
        LocalDateTime participatedAt
    ) {
        this.member = member;
        this.participatedAt = participatedAt;
    }

    protected void validateParticipation() {
        validateNullData(member, "회원은 필수값입니다.");
        validateNullData(participatedAt, "참여 시각은 필수값입니다.");
    }
}
