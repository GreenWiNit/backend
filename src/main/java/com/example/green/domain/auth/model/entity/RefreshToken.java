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
@Table(name = "REFRESH_TOKEN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RefreshToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REFRESH_TOKEN_ID")
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
	private String ipAddress;

	@Column(name = "IS_REVOKED", nullable = false)
	private Boolean isRevoked = false;

	@Column(name = "TOKEN_VERSION", nullable = false)
	private Long tokenVersion = 1L; // AccessToken 무효화를 위한 토큰 버전

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEMBER_ID", nullable = false)
	private Member member;

	public static RefreshToken create(String tokenValue, LocalDateTime expiresAt, Member member,
		String deviceInfo, String ipAddress) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.tokenValue = tokenValue;
		refreshToken.expiresAt = expiresAt;
		refreshToken.member = member;
		refreshToken.deviceInfo = deviceInfo;
		refreshToken.ipAddress = ipAddress;
		refreshToken.isRevoked = false;
		refreshToken.tokenVersion = 1L;
		return refreshToken;
	}

	public void revoke() {
		this.isRevoked = true;
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
		this.tokenVersion += 1000L; // 충분히 큰 값으로 증가시켜 모든 기존 토큰 무효화
		return this.tokenVersion;
	}
}
