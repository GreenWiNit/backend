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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.admin.exception.AdminExceptionMessage;
import com.example.green.domain.auth.admin.repository.AdminRepository;
import com.example.green.domain.auth.entity.TokenManager;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.security.PrincipalDetails;

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
	private final AdminRepository adminRepository;
	private final ApplicationEventPublisher eventPublisher;

	public TokenService(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-expiration:900000}") Long accessTokenExpiration,
		@Value("${jwt.refresh-expiration:604800000}") Long refreshTokenExpiration,
		@Value("${jwt.temp-expiration:600000}") Long tempTokenExpiration,
		RefreshTokenRepository refreshTokenRepository,
		MemberService memberService,
		AdminRepository adminRepository,
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
		this.adminRepository = adminRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * 사용자의 최신 tokenVersion 조회
	 */
	private Long getCurrentTokenVersion(String memberKey) {
		List<TokenManager> tokens = refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey);
		if (tokens.isEmpty()) {
			return 1L; // 기본값
		}
		// 가장 최신 tokenVersion 반환
		return tokens.stream()
			.mapToLong(TokenManager::getTokenVersion)
			.max()
			.orElse(1L);
	}

	public String createAccessToken(String memberKey, String role) {
		try {
			Long tokenVersion;
			
			// 어드민 계정의 경우 기본 토큰 버전 사용 (RefreshToken 미사용)
			if (memberKey.startsWith("admin_")) {
				tokenVersion = 1L;
			} else {
				// 일반 사용자: RefreshToken에서 최신 tokenVersion 조회
				tokenVersion = getCurrentTokenVersion(memberKey);
				if (tokenVersion == null) {
					throw new BusinessException(GlobalExceptionMessage.JWT_CREATION_FAILED);
				}
			}

			return Jwts.builder()
				.claim(CLAIM_USERNAME, memberKey)
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

	public String createRefreshToken(String memberKey, String deviceInfo, String ipAddress) {
		try {
			String tokenValue = Jwts.builder()
				.claim(CLAIM_USERNAME, memberKey)
				.claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
				.signWith(secretKey)
				.compact();

			Member member = memberService.findActiveByMemberKey(memberKey)
				.orElseThrow(() -> {
					log.error("TokenManager 생성 실패: 활성 사용자를 찾을 수 없음 (탈퇴했거나 존재하지 않음) - {}", memberKey);
					return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
				});

			// 기존 토큰 정리 (선택적: 한 사용자당 최대 토큰 수 제한)
			cleanupOldTokens(memberKey);

			LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
			TokenManager tokenManager = TokenManager.create(tokenValue, expiresAt, member, deviceInfo, ipAddress);
			refreshTokenRepository.save(tokenManager);

			log.info("TokenManager 생성 및 DB 저장 완료: {} (IP: {})", memberKey, ipAddress);
			return tokenValue;
		} catch (Exception e) {
			log.error("TokenManager 생성 실패: {}", e.getMessage());
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

	public boolean validateRefreshToken(String tokenValue) {
		try {
			if (!validateToken(tokenValue)) {
				return false;
			}
			TokenManager tokenManager = refreshTokenRepository.findByTokenValueAndNotRevoked(tokenValue)
				.orElse(null);

			return tokenManager != null && tokenManager.isValid();
		} catch (Exception e) {
			log.error("TokenManager 검증 실패: {}", e.getMessage());
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

			// 4. 어드민 계정의 경우 기본 검증만 수행 (RefreshToken 미사용)
			if (username.startsWith("admin_")) {
				return tokenVersion.equals(1L); // 어드민은 항상 tokenVersion 1
			}

			// 5. 일반 사용자: RefreshToken에서 현재 tokenVersion 조회
			Long currentTokenVersion = getCurrentTokenVersion(username);

			if (currentTokenVersion == null) {
				log.debug("사용자의 토큰을 찾을 수 없음: {}", username);
				return false;
			}

			// 6. tokenVersion 비교
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

	public String refreshAccessToken(String refreshToken, String role, String currentIpAddress) {
		if (!validateRefreshToken(refreshToken)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String type = getTokenType(refreshToken);
		if (!TOKEN_TYPE_REFRESH.equals(type)) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}

		String username = getUsername(refreshToken);

		// 최근 접속 정보 업데이트
		refreshTokenRepository.findByTokenValueAndNotRevoked(refreshToken)
			.ifPresent(token -> {
				token.updateLastUsedInfo(currentIpAddress);
				refreshTokenRepository.save(token);
				log.debug("TokenManager 최근 접속 정보 업데이트: {} (IP: {})", username, currentIpAddress);
			});

		return createAccessToken(username, role);
	}

	public void revokeRefreshToken(String tokenValue) {
		refreshTokenRepository.findByTokenValueAndNotRevoked(tokenValue)
			.ifPresent(TokenManager::revoke);
		log.info("TokenManager 무효화 완료: {}", tokenValue.substring(0, 20) + "...");
	}

	public void revokeAllRefreshTokens(String memberKey) {
		// memberKey로 직접 무효화
		refreshTokenRepository.revokeAllByMemberKey(memberKey);
		log.info("사용자의 모든 TokenManager 무효화 완료: {}", memberKey);
	}

	/**
	 * 락 점유 시간 최소화: 핵심 로직만 비관적 락으로 보호
	 * 정리 작업은 트랜잭션 커밋 후 이벤트로 처리
	 *
	 * TODO: 향후 소프트 딜리트 방식으로 개선 예정
	 */
	private void cleanupOldTokens(String memberKey) {
		Optional<Member> memberOpt = memberService.findActiveByMemberKey(memberKey);
		if (memberOpt.isEmpty()) {
			log.warn("토큰 정리 실패: 활성 사용자를 찾을 수 없음 (탈퇴했거나 존재하지 않음) - {}", memberKey);
			return;
		}
		Member member = memberOpt.get();

		List<TokenManager> tokens = refreshTokenRepository.findAllByMemberForCleanupWithLock(member);

		int maxSessions = 3; // 3대 디바이스 허용
		if (tokens.size() < maxSessions) {
			return; // 무효화할 토큰 없음
		}

		// 새 로그인 시 오래된 토큰 무효화 (3대 디바이스 제한)
		int tokensToRevokeCount = tokens.size() - maxSessions + 1;
		// 1. 오래된 TokenManager 무효화
		tokens.stream()
			.limit(tokensToRevokeCount)
			.forEach(TokenManager::revoke);

		// 2. 남은 유효한 토큰들의 tokenVersion 증가 -> 기존 AccessToken 무효화
		Long maxTokenVersion = null;
		List<TokenManager> remainingTokens = tokens.stream()
			.skip(tokensToRevokeCount)
			.toList();
		for (TokenManager token : remainingTokens) {
			Long newTokenVersion = token.logout(); // tokenVersion++
			maxTokenVersion = newTokenVersion;
		}

		// 3. 변경된 토큰들 저장
		refreshTokenRepository.saveAll(remainingTokens);

		// 이벤트 발행: 트랜잭션 커밋 후 정리 작업 예약
		eventPublisher.publishEvent(new TokenCleanupEvent(null, memberKey)); // memberId 불필요 (Auth 도메인 독립성)

		log.info("3대 디바이스 제한 - 토큰 무효화 완료: {} (TokenManager: {}개 revoke, AccessToken: tokenVersion {}로 무효화)",
			memberKey, tokensToRevokeCount, maxTokenVersion);
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

	/**
	 * Access Token으로부터 Authentication 객체 생성
	 * - username으로 Member 또는 Admin 조회 -> memberId/adminId 포함하도록
	 */
	public Authentication createAuthentication(String token) {
		try {
			String username = getUsername(token);
			String role = getRole(token);

			// 어드민 계정 처리 (admin_ prefix로 구분)
			if (username.startsWith("admin_")) {
				String adminLoginId = username.substring(6); // "admin_" prefix 제거
				Admin admin = adminRepository.findByLoginId(adminLoginId)
					.orElseThrow(() -> {
						log.error("JWT 토큰 검증 중 관리자를 찾을 수 없음: {}", adminLoginId);
						return new BusinessException(AdminExceptionMessage.ADMIN_NOT_FOUND);
					});

				PrincipalDetails principal = new PrincipalDetails(
					admin.getId(),       // adminId
					username,            // admin_ prefix가 포함된 username
					role,                // 권한 (ROLE_ADMIN, ROLE_SUPER_ADMIN)
					admin.getName(),     // 실제 관리자 이름
					admin.getEmail()     // 관리자 이메일
				);

				return new UsernamePasswordAuthenticationToken(
					principal,
					null,
					principal.getAuthorities()
				);
			}

			// 일반 사용자 처리 (활성 회원만 조회 - 탈퇴한 회원의 토큰 자동 무효화)
			Member member = memberService.findActiveByMemberKey(username)
				.orElseThrow(() -> {
					log.error("JWT 토큰 검증 중 활성 사용자를 찾을 수 없음 (탈퇴했거나 존재하지 않음): {}", username);
					return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
				});

			PrincipalDetails principal = new PrincipalDetails(
				member.getId(),       // memberId
				username,             // OAuth2 username (provider+providerId)
				role,                 // 권한
				member.getName(),     // 실제 사용자 이름 (프로바이더로 콜백 받은)
				member.getEmail()     // 회원 이메일
			);

			return new UsernamePasswordAuthenticationToken(
				principal,
				null,
				principal.getAuthorities()
			);
		} catch (Exception e) {
			log.error("Authentication 생성 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_PARSING_FAILED);
		}
	}
}
