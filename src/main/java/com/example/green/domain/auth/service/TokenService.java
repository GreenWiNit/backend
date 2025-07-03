package com.example.green.domain.auth.service;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.model.entity.RefreshToken;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.service.MemberService;
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

	private static final String CLAIM_USERNAME = "username";
	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_TYPE = "type";
	private static final String CLAIM_TOKEN_VERSION = "tokenVersion";
	private static final String CLAIM_EMAIL = "email";
	private static final String CLAIM_NAME = "name";
	private static final String CLAIM_PROFILE_IMAGE_URL = "profileImageUrl";
	private static final String CLAIM_PROVIDER = "provider";
	private static final String CLAIM_PROVIDER_ID = "providerId";

	private final SecretKey secretKey;

	@Value("${jwt.access-expiration:900000}")
	private final Long accessTokenExpiration; // 15분

	@Value("${jwt.refresh-expiration:604800000}")
	private final Long refreshTokenExpiration; // 7일

	@Value("${jwt.temp-expiration:600000}")
	private final Long tempTokenExpiration; // 10분

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberService memberService;
	private final ApplicationEventPublisher eventPublisher;

	public TokenService(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-expiration:900000}") Long accessTokenExpiration,
		@Value("${jwt.refresh-expiration:604800000}") Long refreshTokenExpiration,
		@Value("${jwt.temp-expiration:600000}") Long tempTokenExpiration,
		RefreshTokenRepository refreshTokenRepository,
		MemberService memberService,
		ApplicationEventPublisher eventPublisher) {

		this.secretKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm()
		);
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
		this.tempTokenExpiration = tempTokenExpiration;
		this.refreshTokenRepository = refreshTokenRepository;
		this.memberService = memberService;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * 사용자의 최신 tokenVersion 조회 (RefreshToken 기반)
	 * MemberService 의존성 제거를 위해 Auth 도메인 내에서 해결
	 */
	private Long getCurrentTokenVersion(String username) {
		List<RefreshToken> tokens = refreshTokenRepository.findAllByUsernameAndNotRevoked(username);
		if (tokens.isEmpty()) {
			return 1L; // 기본값
		}
		// 가장 최신 tokenVersion 반환
		return tokens.stream()
			.mapToLong(RefreshToken::getTokenVersion)
			.max()
			.orElse(1L);
	}

	public String createAccessToken(String username, String role) {
		try {
			// RefreshToken에서 최신 tokenVersion 조회 (Auth 도메인 독립성)
			Long tokenVersion = getCurrentTokenVersion(username);
			if (tokenVersion == null) {
				throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
			}

			return Jwts.builder()
				.claim(CLAIM_USERNAME, username)
				.claim(CLAIM_ROLE, role)
				.claim(CLAIM_TYPE, TOKEN_TYPE_ACCESS)
				.claim(CLAIM_TOKEN_VERSION, tokenVersion)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			log.error("AccessToken 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
		}
	}

	public String createRefreshToken(String username, String deviceInfo, String ipAddress) {
		try {
			String tokenValue = Jwts.builder()
				.claim(CLAIM_USERNAME, username)
				.claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
				.signWith(secretKey)
				.compact();

			// 사용자 조회 (RefreshToken 엔티티 생성에 필요)
			Member member = memberService.findByUsername(username)
				.orElseThrow(() -> {
					log.error("RefreshToken 생성 실패: 사용자를 찾을 수 없음 - {}", username);
					return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
				});

			// 기존 토큰 정리 (선택적: 한 사용자당 최대 토큰 수 제한)
			cleanupOldTokens(username);

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

	public String createTemporaryToken(String email, String name, String profileImageUrl, String provider,
		String providerId) {
		try {
			return Jwts.builder()
				.claim(CLAIM_EMAIL, email)
				.claim(CLAIM_NAME, name)
				.claim(CLAIM_PROFILE_IMAGE_URL, profileImageUrl)
				.claim(CLAIM_PROVIDER, provider)
				.claim(CLAIM_PROVIDER_ID, providerId)
				.claim(CLAIM_TYPE, TOKEN_TYPE_TEMP)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + tempTokenExpiration))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			log.error("임시 토큰 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
		}
	}

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

	public String getUsername(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(CLAIM_USERNAME, String.class);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰에서 username 추출 시도: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("JWT username 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	public String getRole(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(CLAIM_ROLE, String.class);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰에서 role 추출 시도: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("JWT role 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	public String getTokenType(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(CLAIM_TYPE, String.class);
		} catch (JwtException e) {
			log.error("JWT 타입 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	public Long getTokenVersion(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(CLAIM_TOKEN_VERSION, Long.class);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰에서 tokenVersion 추출 시도: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("JWT tokenVersion 추출 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	/**
	 * AccessToken 완전 검증 (JWT 형식 + tokenVersion 비교)
	 * 로그아웃 후 AccessToken 재사용 방지
	 */
	public boolean validateAccessToken(String accessToken) {
		try {
			// 1. JWT 형식 검증
			if (!validateToken(accessToken)) {
				return false;
			}

			// 2. AccessToken 타입 확인
			String tokenType = getTokenType(accessToken);
			if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
				return false;
			}

			// 3. 사용자명과 토큰 버전 추출
			String username = getUsername(accessToken);
			Long tokenVersion = getTokenVersion(accessToken);

			// 4. RefreshToken에서 현재 tokenVersion 조회 (Auth 도메인 독립성)
			Long currentTokenVersion = getCurrentTokenVersion(username);

			if (currentTokenVersion == null) {
				log.debug("사용자의 토큰을 찾을 수 없음: {}", username);
				return false;
			}

			// 5. tokenVersion 비교
			boolean isValidVersion = currentTokenVersion.equals(tokenVersion);
			if (!isValidVersion) {
				log.debug("토큰 버전 불일치 - DB: {}, Token: {} (사용자: {})",
					currentTokenVersion, tokenVersion, username);
			}

			return isValidVersion;

		} catch (Exception e) {
			log.error("AccessToken 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public TempTokenInfoDto extractTempTokenInfo(String tempToken) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(tempToken)
				.getPayload();

			String type = claims.get(CLAIM_TYPE, String.class);
			if (!TOKEN_TYPE_TEMP.equals(type)) {
				throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
			}

			return TempTokenInfoDto.builder()
				.email(claims.get(CLAIM_EMAIL, String.class))
				.name(claims.get(CLAIM_NAME, String.class))
				.profileImageUrl(claims.get(CLAIM_PROFILE_IMAGE_URL, String.class))
				.provider(claims.get(CLAIM_PROVIDER, String.class))
				.providerId(claims.get(CLAIM_PROVIDER_ID, String.class))
				.build();
		} catch (ExpiredJwtException e) {
			log.debug("만료된 임시 토큰: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_TOKEN_EXPIRED);
		} catch (JwtException e) {
			log.error("임시 토큰 파싱 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}

	public String refreshAccessToken(String refreshToken, String role) {
		if (!validateRefreshToken(refreshToken)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String type = getTokenType(refreshToken);
		if (!TOKEN_TYPE_REFRESH.equals(type)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String username = getUsername(refreshToken);
		return createAccessToken(username, role);
	}

	public void revokeRefreshToken(String tokenValue) {
		refreshTokenRepository.findByTokenValueAndNotRevoked(tokenValue)
			.ifPresent(RefreshToken::revoke);
		log.info("RefreshToken 무효화 완료: {}", tokenValue.substring(0, 20) + "...");
	}

	public void revokeAllRefreshTokens(String username) {
		// Auth 도메인 독립성: username으로 직접 무효화
		refreshTokenRepository.revokeAllByUsername(username);
		log.info("사용자의 모든 RefreshToken 무효화 완료: {}", username);
	}

	/**
	 * 락 점유 시간 최소화: 핵심 로직만 비관적 락으로 보호
	 * 정리 작업은 트랜잭션 커밋 후 이벤트로 처리
	 *
	 * TODO: 향후 소프트 딜리트 방식으로 개선 예정
	 */
	private void cleanupOldTokens(String username) {
		Optional<Member> memberOpt = memberService.findByUsername(username);
		if (memberOpt.isEmpty()) {
			log.warn("토큰 정리 실패: 사용자를 찾을 수 없음 - {}", username);
			return;
		}
		Member member = memberOpt.get();

		List<RefreshToken> tokens = refreshTokenRepository.findAllByMemberForCleanupWithLock(member);

		int maxSessions = 3; // 3대 디바이스 허용
		if (tokens.size() < maxSessions) {
			return; // 무효화할 토큰 없음
		}

		// 새 로그인 시 오래된 토큰 무효화 (3대 디바이스 제한)
		int tokensToRevokeCount = tokens.size() - maxSessions + 1;
		// 1. 오래된 RefreshToken 무효화
		tokens.stream()
			.limit(tokensToRevokeCount)
			.forEach(RefreshToken::revoke);

		// 2. 남은 유효한 토큰들의 tokenVersion 증가 -> 기존 AccessToken 무효화
		Long maxTokenVersion = null;
		List<RefreshToken> remainingTokens = tokens.stream()
			.skip(tokensToRevokeCount)
			.toList();
		for (RefreshToken token : remainingTokens) {
			Long newTokenVersion = token.logout(); // tokenVersion++
			maxTokenVersion = newTokenVersion;
		}

		// 3. 변경된 토큰들 저장
		refreshTokenRepository.saveAll(remainingTokens);

		// 이벤트 발행: 트랜잭션 커밋 후 정리 작업 예약
		eventPublisher.publishEvent(new TokenCleanupEvent(null, username)); // memberId 불필요 (Auth 도메인 독립성)

		log.info("3대 디바이스 제한 - 토큰 무효화 완료: {} (RefreshToken: {}개 revoke, AccessToken: tokenVersion {}로 무효화)",
			username, tokensToRevokeCount, maxTokenVersion);
	}

	// 토큰 정리 이벤트
	public static class TokenCleanupEvent {
		private final Long memberId;
		private final String memberUsername;

		public TokenCleanupEvent(Long memberId, String memberUsername) {
			this.memberId = memberId;
			this.memberUsername = memberUsername;
		}

		public String getMemberUsername() {
			return memberUsername;
		}
	}
}
