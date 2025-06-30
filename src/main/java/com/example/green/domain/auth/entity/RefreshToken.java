package com.example.green.domain.auth.entity;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REFRESH_TOKEN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REFRESH_TOKEN_ID")
	private Long id;

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
		return refreshToken;
	}

	// 토큰 무효화
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
} 