package com.example.green.domain.user.member;

import com.example.green.domain.common.BaseEntity;
import io.awspring.cloud.autoconfigure.core.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "user_table")
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Embedded  private Profile profile;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime lastLoginAt;

    @Builder
    private Member(
            Profile profile,
            MemberStatus status,
            MemberRole role,
            LocalDateTime lastLoginAt) {
        this.profile = profile;
        this.status = status;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
    }

    public static Member createNormalMember(Profile profile) {
        return new Member(
                profile,
                MemberStatus.NORMAL,
                MemberRole.USER,
                LocalDateTime.now());
    }

}
