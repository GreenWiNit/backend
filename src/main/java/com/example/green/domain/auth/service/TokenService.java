package com.example.green.domain.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.entity.RefreshToken;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TokenService {

	private final SecretKey secretKey;
	private final Long accessTokenExpiration; // 15분
	private final Long refreshTokenExpiration; // 7일
	private final Long tempTokenExpiration; // 10분

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;

	public TokenService(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-expiration:900000}") Long accessTokenExpiration, // 15분 기본값
		@Value("${jwt.refresh-expiration:604800000}") Long refreshTokenExpiration, // 7일 기본값
		@Value("${jwt.temp-expiration:600000}") Long tempTokenExpiration, // 10분 기본값
		RefreshTokenRepository refreshTokenRepository,
		MemberRepository memberRepository) {

		this.secretKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm()
		);
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
		this.tempTokenExpiration = tempTokenExpiration;
		this.refreshTokenRepository = refreshTokenRepository;
		this.memberRepository = memberRepository;
	}

	// AccessToken 생성 (짧은 유효기간)
	public String createAccessToken(String username, String role) {
		try {
			return Jwts.builder()
				.claim("username", username)
				.claim("role", role)
				.claim("type", "access")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			log.error("AccessToken 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
		}
	}

	// RefreshToken 생성 (긴 유효기간) - DB 저장
	public String createRefreshToken(String username, String deviceInfo, String ipAddress) {
		try {
			String tokenValue = Jwts.builder()
				.claim("username", username)
				.claim("type", "refresh")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
				.signWith(secretKey)
				.compact();

			// 사용자 조회
			Member member = memberRepository.findOptionalByUsername(username)
				.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));

			// 기존 토큰 정리 (선택적: 한 사용자당 최대 토큰 수 제한)
			cleanupOldTokens(member);

			// DB에 RefreshToken 저장
			LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
			RefreshToken refreshToken = RefreshToken.create(tokenValue, expiresAt, member, deviceInfo, ipAddress);
			refreshTokenRepository.save(refreshToken);

			log.info("RefreshToken 생성 및 DB 저장 완료: {} (IP: {})", username, ipAddress);
			return tokenValue;
		} catch (Exception e) {
			log.error("RefreshToken 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
		}
	}

	// 임시 토큰 생성 (신규 사용자용)
	public String createTemporaryToken(String email, String name, String profileImageUrl, String provider,
		String providerId) {
		try {
			return Jwts.builder()
				.claim("email", email)
				.claim("name", name)
				.claim("profileImageUrl", profileImageUrl)
				.claim("provider", provider)
				.claim("providerId", providerId)
				.claim("type", "temp")
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + tempTokenExpiration))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			log.error("임시 토큰 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
		}
	}

	// 토큰 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("잘못된 형식의 JWT 토큰: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 비어있음: {}", e.getMessage());
		} catch (JwtException e) {
			log.error("JWT 토큰 검증 실패: {}", e.getMessage());
		}
		return false;
	}

	// RefreshToken 검증 (DB 조회 포함)
	public boolean validateRefreshToken(String tokenValue) {
		try {
			// 먼저 JWT 형식 검증
			if (!validateToken(tokenValue)) {
				return false;
			}

			// DB에서 토큰 조회 및 검증
			RefreshToken refreshToken = refreshTokenRepository.findByTokenValueAndNotRevoked(tokenValue)
				.orElse(null);

			return refreshToken != null && refreshToken.isValid();
		} catch (Exception e) {
			log.error("RefreshToken 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	// 토큰에서 username 추출
	public String getUsername(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("username", String.class);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰에서 username 추출 시도: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("JWT username 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	// 토큰에서 role 추출
	public String getRole(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰에서 role 추출 시도: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("JWT role 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	// 토큰에서 타입 추출
	public String getTokenType(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("type", String.class);
		} catch (JwtException e) {
			log.error("JWT 타입 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	// 임시 토큰에서 사용자 정보 추출
	public TempTokenInfoDto extractTempTokenInfo(String tempToken) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(tempToken)
				.getPayload();

			String type = claims.get("type", String.class);
			if (!"temp".equals(type)) {
				throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
			}

			return TempTokenInfoDto.builder()
				.email(claims.get("email", String.class))
				.name(claims.get("name", String.class))
				.profileImageUrl(claims.get("profileImageUrl", String.class))
				.provider(claims.get("provider", String.class))
				.providerId(claims.get("providerId", String.class))
				.build();
		} catch (ExpiredJwtException e) {
			log.debug("만료된 임시 토큰: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("임시 토큰 파싱 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	// RefreshToken으로 새 AccessToken 발급
	public String refreshAccessToken(String refreshToken, String role) {
		if (!validateRefreshToken(refreshToken)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String type = getTokenType(refreshToken);
		if (!"refresh".equals(type)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String username = getUsername(refreshToken);
		return createAccessToken(username, role);
	}

	// RefreshToken 무효화 (로그아웃)
	public void revokeRefreshToken(String tokenValue) {
		refreshTokenRepository.findByTokenValueAndNotRevoked(tokenValue)
			.ifPresent(RefreshToken::revoke);
		log.info("RefreshToken 무효화 완료: {}", tokenValue.substring(0, 20) + "...");
	}

	// 사용자의 모든 RefreshToken 무효화 (모든 디바이스 로그아웃)
	public void revokeAllRefreshTokens(String username) {
		Member member = memberRepository.findOptionalByUsername(username)
			.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));

		refreshTokenRepository.revokeAllByMember(member);
		log.info("사용자의 모든 RefreshToken 무효화 완료: {}", username);
	}

	// 오래된 토큰 정리 (한 사용자당 최대 5개 세션 유지)
	private void cleanupOldTokens(Member member) {
		List<RefreshToken> tokens = refreshTokenRepository.findAllByMemberAndNotRevoked(member);

		// 최대 세션 수 제한 (예: 5개)
		int maxSessions = 5;
		if (tokens.size() >= maxSessions) {
			// 가장 오래된 토큰들 무효화 (ID 기준 정렬 - 낮은 ID가 더 오래된 것)
			tokens.stream()
				.sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
				.limit(tokens.size() - maxSessions + 1)
				.forEach(RefreshToken::revoke);
		}

		// 만료된 토큰 삭제
		refreshTokenRepository.deleteExpiredAndRevokedTokensByMember(member, LocalDateTime.now());
	}
}
