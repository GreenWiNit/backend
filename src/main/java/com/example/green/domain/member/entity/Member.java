package com.example.green.domain.member.entity;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.entity.vo.Profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	indexes = {
		@Index(name = "idx_member_nickname_active", 
			   columnList = "nickname, status, deleted")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Version
	private Long version;

	@Column(unique = true, nullable = false)
	private String memberKey;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	/**
	 * 전화번호 컬럼 추가 (nullable = true)
	 */
	@Column(name = "phone_number", nullable = true, length = 20)
	private String phoneNumber;

	@Embedded
	private Profile profile;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	private LocalDateTime lastLoginAt;

	private Member(String memberKey, String name, String email, String nickname) {
		this.memberKey = memberKey;
		this.name = name;
		this.email = email;
		this.profile = Profile.builder()
			.nickname(nickname)
			.profileImageUrl(null)
			.build();
		this.status = MemberStatus.NORMAL;
		this.role = MemberRole.USER;
		this.lastLoginAt = LocalDateTime.now();
	}

	public static Member create(String memberKey, String name, String email) {
		return new Member(memberKey, name, email, null);
	}
	
	public static Member create(String memberKey, String name, String email, String nickname) {
		return new Member(memberKey, name, email, nickname);
	}

	public void updateOAuth2Info(String name, String email) {
		this.name = name;
		this.email = email;
		this.lastLoginAt = LocalDateTime.now();
	}

	public void updateProfile(String nickname, String profileImageUrl) {
		this.profile = this.profile.update(nickname, profileImageUrl);
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void withdraw() {
		this.status = MemberStatus.DELETED;
		this.lastLoginAt = null; // 마지막 로그인 시간 초기화
		this.markDeleted();
	}

	public boolean isWithdrawn() {
		return this.status == MemberStatus.DELETED || this.isDeleted();
	}

}
