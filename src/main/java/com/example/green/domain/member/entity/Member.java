package com.example.green.domain.member.entity;

import java.time.LocalDateTime;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.global.utils.UlidUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
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

	@Column(unique = true, nullable = false, updatable = false, length = 26) //ULID
	private String memberCode;

	@Version
	private Long version; //낙관적 락을 위한 컬럼, JPA가 자동으로 관리하며, 업데이트 시마다 증가

	@Column(unique = true, nullable = false) // OAuth2 Provider와 Provider ID 조합으로 생성된 고유 식별자
	private String username;

	@Column(nullable = false) // OAuth2에서 제공받은 사용자 실명
	private String name;

	@Column(nullable = false) // OAuth2에서 제공받은 사용자 이메일
	private String email;

	@Embedded
	private Profile profile;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	private LocalDateTime lastLoginAt;

	private Member(String username, String name, String email) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.memberCode = UlidUtils.generate();
		this.profile = new Profile(name, null); // OAuth2 name을 기본 닉네임으로 설정
		this.status = MemberStatus.NORMAL;
		this.role = MemberRole.USER;
		this.lastLoginAt = LocalDateTime.now();
	}

	/**
	 * OAuth2 로그인으로 회원을 생성합니다.
	 *
	 * @param username OAuth2 Provider와 Provider ID 조합 (예: "google 123456789")
	 * @param name OAuth2에서 제공받은 사용자 실명
	 * @param email OAuth2에서 제공받은 사용자 이메일
	 * @return 생성된 Member 엔티티
	 */
	public static Member create(String username, String name, String email) {
		return new Member(username, name, email);
	}

	/**
	 *  OAuth2 정보 업데이트 (낙관적 락 적용)
	 * 동시 업데이트 시 OptimisticLockException 발생
	 */
	public void updateOAuth2Info(String name, String email) {
		this.name = name;
		this.email = email;
		this.lastLoginAt = LocalDateTime.now();
		// @Version이 자동으로 증가됨
	}

	public void updateNickname(String nickname) {
		if (nickname != null && !nickname.trim().isEmpty()) {
			this.profile = new Profile(nickname, this.profile.getProfileImageUrl());
		}
	}

	public void updateProfileImage(String profileImageUrl) {
		this.profile = new Profile(this.profile.getNickname(), profileImageUrl);
	}

}

