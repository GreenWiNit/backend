package com.example.green.domain.auth.admin.entity;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.green.domain.auth.admin.entity.enums.AdminStatus;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ADMIN")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	private Long id;

	@Column(unique = true, nullable = false, length = 50, name = "login_id")
	private String loginId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 20)
	private String name; //관리자의 실명을 저장

	@Column(nullable = false, length = 100)
	private String email;

	@Enumerated(EnumType.STRING)
	private AdminStatus status;

	private LocalDateTime lastLoginAt;

	private Admin(String loginId, String password, String name, String email) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.status = AdminStatus.ACTIVE;
	}

	/**
	 * 비밀번호 검증
	 */
	public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(rawPassword, this.password);
	}

	/**
	 * 로그인 시간 업데이트
	 */
	public void updateLastLogin() {
		this.lastLoginAt = LocalDateTime.now();
	}

	/**
	 * 계정 활성화 여부 확인
	 */
	public boolean isActive() {
		return status == AdminStatus.ACTIVE;
	}

	/**
	 * JWT 토큰용 memberKey 생성 (auth 도메인과 구분하기 위함)
	 */
	public String getTokenMemberKey() {
		return "admin_" + this.loginId;
	}
} 