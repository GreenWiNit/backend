package com.example.green.domain.auth.admin.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.admin.entity.enums.AdminStatus;

class AdminTest {

	private PasswordEncoder passwordEncoder;
	private Admin admin;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		admin = createTestAdmin();
	}

	@Test
	@DisplayName("Admin 생성 시 기본값이 정상적으로 설정된다")
	void admin_생성시_기본값이_설정된다() {
		// when & then
		assertThat(admin.getLoginId()).isEqualTo("testAdmin");
		assertThat(admin.getName()).isEqualTo("관리자");
		assertThat(admin.getEmail()).isEqualTo("admin@test.com");
		assertThat(admin.getStatus()).isEqualTo(AdminStatus.ACTIVE);
		assertThat(admin.getLastLoginAt()).isNull();
	}

	@Test
	@DisplayName("올바른 비밀번호로 검증 시 true를 반환한다")
	void 올바른_비밀번호_검증_성공() {
		// given
		String rawPassword = "Test@1234!";

		// when
		boolean result = admin.verifyPassword(rawPassword, passwordEncoder);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("틀린 비밀번호로 검증 시 false를 반환한다")
	void 틀린_비밀번호_검증_실패() {
		// given
		String wrongPassword = "wrongpassword";

		// when
		boolean result = admin.verifyPassword(wrongPassword, passwordEncoder);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("로그인 시간이 현재 시간으로 업데이트된다")
	void 로그인_시간_업데이트() {
		// given
		LocalDateTime beforeUpdate = LocalDateTime.now();

		// when
		admin.updateLastLogin();

		// then
		assertThat(admin.getLastLoginAt()).isAfter(beforeUpdate);
		assertThat(admin.getLastLoginAt()).isBefore(LocalDateTime.now().plusSeconds(1));
	}

	@Test
	@DisplayName("ACTIVE 상태인 어드민은 활성화된 것으로 판단된다")
	void ACTIVE_상태_어드민은_활성화된다() {
		// when & then
		assertThat(admin.isActive()).isTrue();
	}

	@Test
	@DisplayName("JWT 토큰용 username에 admin_ prefix가 추가된다")
	void JWT_토큰용_username_생성() {
		// when
		String tokenUsername = admin.getTokenMemberKey();

		// then
		assertThat(tokenUsername).isEqualTo("admin_testAdmin");
	}

	@Test
	@DisplayName("ROLE_ADMIN 상수가 올바르게 정의되어 있다")
	void ROLE_ADMIN_상수_확인() {
		// when & then
		assertThat(Admin.ROLE_ADMIN).isEqualTo("ROLE_ADMIN");
	}

	private Admin createTestAdmin() {
		// BCrypt로 암호화
		String encodedPassword = passwordEncoder.encode("Test@1234!");

		try {
			var constructor = Admin.class.getDeclaredConstructor(String.class, String.class, String.class, String.class);
			constructor.setAccessible(true);
			return constructor.newInstance(
				"testAdmin", encodedPassword, "관리자", "admin@test.com"
			);
		} catch (Exception e) {
			throw new RuntimeException("테스트용 Admin 생성 실패", e);
		}
	}
}