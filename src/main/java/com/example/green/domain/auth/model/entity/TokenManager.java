package com.example.green.domain.auth.model.entity;

import java.time.LocalDateTime;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TOKEN_MANAGER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TokenManager extends BaseEntity {

	private static final Long LOGOUT_ALL_DEVICES_VERSION_INCREMENT = 1000L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TOKEN_MANAGER_ID")
	@EqualsAndHashCode.Include
	private Long id;

	@Version
	private Long version; //낙관적 락을 위한 버전 컬럼. 토큰 무효화 시 동시성 보장

	@Column(name = "TOKEN_VALUE", nullable = false, unique = true, length = 512)
	private String tokenValue;

	@Column(name = "EXPIRES_AT", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "DEVICE_INFO", length = 200)
	private String deviceInfo;

	@Column(name = "IP_ADDRESS", length = 45)
	private String ipAddress; // 최근 접속 IP (토큰 사용 시마다 업데이트)

	@Column(name = "LAST_USED_AT")
	private LocalDateTime lastUsedAt; // 마지막 사용 시간 (토큰 갱신 시마다 업데이트)

	@Column(name = "IS_REVOKED", nullable = false)
	private Boolean isRevoked = false;

	@Column(name = "TOKEN_VERSION", nullable = false)
	private Long tokenVersion = 1L; // AccessToken 무효화를 위한 토큰 버전

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_ID", nullable = false)
	private Member member;

	public static TokenManager create(String tokenValue, LocalDateTime expiresAt, Member member,
		String deviceInfo, String ipAddress) {
		TokenManager tokenManager = new TokenManager();
		tokenManager.tokenValue = tokenValue;
		tokenManager.expiresAt = expiresAt;
		tokenManager.member = member;
		tokenManager.deviceInfo = deviceInfo;
		tokenManager.ipAddress = ipAddress;
		tokenManager.lastUsedAt = LocalDateTime.now();
		tokenManager.isRevoked = false;
		tokenManager.tokenVersion = 1L;
		return tokenManager;
	}

	public void revoke() {
		this.isRevoked = true;
	}

	// 토큰 사용 시 최근 접속 정보 업데이트
	public void updateLastUsedInfo(String currentIpAddress) {
		this.ipAddress = currentIpAddress;
		this.lastUsedAt = LocalDateTime.now();
	}

	// 토큰 유효성 검증
	public boolean isValid() {
		return !isRevoked && expiresAt.isAfter(LocalDateTime.now());
	}

	// 만료 여부 확인
	public boolean isExpired() {
		return expiresAt.isBefore(LocalDateTime.now());
	}

	// 단일 디바이스 로그아웃: 현재 RefreshToken의 tokenVersion 증가
	public Long logout() {
		this.tokenVersion++;
		return this.tokenVersion;
	}

	// 모든 디바이스 로그아웃: tokenVersion을 크게 증가시켜 모든 AccessToken 무효화
	public Long logoutAllDevices() {
		this.tokenVersion += LOGOUT_ALL_DEVICES_VERSION_INCREMENT; // 충분히 큰 값으로 증가시켜 모든 기존 토큰 무효화 시킴.
		return this.tokenVersion;
	}
}
