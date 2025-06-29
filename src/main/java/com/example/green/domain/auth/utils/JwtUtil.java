package com.example.green.domain.auth.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	private final SecretKey secretKey;
	private final Long expiration;

	public JwtUtil(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.expiration}") Long expiration) {

		this.secretKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm()
		);
		this.expiration = expiration;
	}

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

	public Boolean isExpired(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(new Date());
		} catch (ExpiredJwtException e) {
			return true; // 이미 만료된 토큰
		} catch (JwtException e) {
			log.error("JWT 만료 검증 실패: {}", e.getMessage());
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}
	}

	public String createJwt(String username, String role) {
		try {
			return Jwts.builder()
				.claim("username", username)
				.claim("role", role)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			log.error("JWT 생성 실패: {}", e.getMessage());
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
}

