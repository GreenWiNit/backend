package com.example.green.domain.member;

import com.example.green.domain.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(
            String nickname,
            String profileImageUrl,
            MemberStatus status,
            MemberRole role,
            LocalDateTime lastLoginAt) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
    }

    public static Member createNormalMember(Profile profile) {
        return Member.builder()
                .nickname(profile.nickname())
                .profileImageUrl(profile.profileImageUrl())
                .status(MemberStatus.NORMAL)
                .role(MemberRole.USER)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
    
    public Profile getProfile() {
        return new Profile(nickname, profileImageUrl);
    }
}