package com.example.green.domain.auth.admin.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.admin.entity.enums.AdminStatus;
import com.example.green.domain.auth.admin.exception.AdminExceptionMessage;
import com.example.green.domain.auth.admin.repository.AdminRepository;
import com.example.green.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 어드민 로그인 인증
	 */
	@Transactional
	public Admin authenticate(String loginId, String password) {
		Admin admin = findActiveAdminByLoginId(loginId);
		
		validatePassword(admin, password);
		
		admin.updateLastLogin();
		adminRepository.save(admin);

		log.info("[ADMIN] 로그인 성공: {} ({})", loginId, admin.getName());
		return admin;
	}

	public Optional<Admin> findByLoginId(String loginId) {
		return adminRepository.findByLoginId(loginId);
	}

	private Admin findActiveAdminByLoginId(String loginId) {
		return adminRepository.findByLoginIdAndStatus(loginId, AdminStatus.ACTIVE)
			.orElseThrow(() -> {
				log.warn("[ADMIN] 로그인 실패 - 존재하지 않는 계정: {}", loginId);
				return new BusinessException(AdminExceptionMessage.ADMIN_NOT_FOUND);
			});
	}

	private void validatePassword(Admin admin, String password) {
		if (!admin.verifyPassword(password, passwordEncoder)) {
			log.warn("[ADMIN] 로그인 실패 - 비밀번호 불일치: {}", admin.getLoginId());
			throw new BusinessException(AdminExceptionMessage.INVALID_PASSWORD);
		}
	}
} 