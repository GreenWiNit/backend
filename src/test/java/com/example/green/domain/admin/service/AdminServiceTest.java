package com.example.green.domain.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.green.domain.admin.entity.Admin;
import com.example.green.domain.admin.entity.enums.AdminStatus;
import com.example.green.domain.admin.exception.AdminExceptionMessage;
import com.example.green.domain.admin.repository.AdminRepository;
import com.example.green.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private AdminRepository adminRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AdminService adminService;

	@Test
	@DisplayName("올바른 로그인 정보로 인증 성공")
	void 올바른_로그인_정보로_인증_성공() {
		// given
		String loginId = "admin1234";
		String password = "admin1234!";

		Admin mockAdmin = createMockAdmin();
		given(adminRepository.findByLoginIdAndStatus(loginId, AdminStatus.ACTIVE))
			.willReturn(Optional.of(mockAdmin));
		given(mockAdmin.verifyPassword(password, passwordEncoder))
			.willReturn(true);

		// when
		Admin result = adminService.authenticate(loginId, password);

		// then
		assertThat(result).isEqualTo(mockAdmin);
		then(mockAdmin).should().updateLastLogin();
		then(adminRepository).should().save(mockAdmin);
	}

	@Test
	@DisplayName("존재하지 않는 로그인 ID로 인증 실패")
	void 존재하지_않는_로그인_ID로_인증_실패() {
		// given
		String loginId = "nonexistent";
		String password = "admin1234!";

		given(adminRepository.findByLoginIdAndStatus(loginId, AdminStatus.ACTIVE))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminService.authenticate(loginId, password))
			.isInstanceOf(BusinessException.class)
			.hasMessage(AdminExceptionMessage.ADMIN_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("비활성화된 어드민 계정으로 인증 실패")
	void 비활성화된_어드민_계정으로_인증_실패() {
		// given
		String loginId = "admin1234";
		String password = "admin1234!";

		given(adminRepository.findByLoginIdAndStatus(loginId, AdminStatus.ACTIVE))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminService.authenticate(loginId, password))
			.isInstanceOf(BusinessException.class)
			.hasMessage(AdminExceptionMessage.ADMIN_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("잘못된 비밀번호로 인증 실패")
	void 잘못된_비밀번호로_인증_실패() {
		// given
		String loginId = "admin1234";
		String wrongPassword = "wrongpassword";

		Admin mockAdmin = createMockAdmin();
		given(adminRepository.findByLoginIdAndStatus(loginId, AdminStatus.ACTIVE))
			.willReturn(Optional.of(mockAdmin));
		given(mockAdmin.verifyPassword(wrongPassword, passwordEncoder))
			.willReturn(false);

		// when & then
		assertThatThrownBy(() -> adminService.authenticate(loginId, wrongPassword))
			.isInstanceOf(BusinessException.class)
			.hasMessage(AdminExceptionMessage.INVALID_PASSWORD.getMessage());

		then(mockAdmin).should(never()).updateLastLogin();
		then(adminRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("loginId로 어드민 조회 성공")
	void loginId로_어드민_조회_성공() {
		// given
		String loginId = "admin1234";
		Admin mockAdmin = createMockAdmin();
		given(adminRepository.findByLoginId(loginId))
			.willReturn(Optional.of(mockAdmin));

		// when
		Optional<Admin> result = adminService.findByLoginId(loginId);

		// then
		assertThat(result).isPresent()
			.get().isEqualTo(mockAdmin);
	}

	@Test
	@DisplayName("loginId로 어드민 조회 시 존재하지 않으면 빈 Optional 반환")
	void loginId로_어드민_조회_시_존재하지_않으면_빈_Optional_반환() {
		// given
		String loginId = "nonexistent";
		given(adminRepository.findByLoginId(loginId))
			.willReturn(Optional.empty());

		// when
		Optional<Admin> result = adminService.findByLoginId(loginId);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("ID로 어드민 조회 성공")
	void ID로_어드민_조회_성공() {
		// given
		Long adminId = 1L;
		Admin mockAdmin = createMockAdmin();
		given(adminRepository.findById(adminId))
			.willReturn(Optional.of(mockAdmin));

		// when
		Optional<Admin> result = adminService.findById(adminId);

		// then
		assertThat(result).isPresent()
			.get().isEqualTo(mockAdmin);
	}

	private Admin createMockAdmin() {
		return mock(Admin.class);
	}
}