package com.example.green.global.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.green.global.security.PrincipalDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditorAwareConfig implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			if (authentication == null || !authentication.isAuthenticated()) {
				log.debug("인증되지 않은 요청에서 Auditor 조회 시도");
				return Optional.of("SYSTEM"); // 인증되지 않은 경우 시스템으로 처리
			}

			// AnonymousAuthenticationToken 체크
			if ("anonymousUser".equals(authentication.getPrincipal())) {
				log.debug("익명 사용자 요청에서 Auditor 조회");
				return Optional.of("ANONYMOUS");
			}

			// JWT 인증된 사용자인 경우
			if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
				String username = principalDetails.getUsername();
				log.debug("현재 Auditor: {}", username);
				return Optional.of(username);
			}

			log.warn("알 수 없는 Principal 타입: {}", authentication.getPrincipal().getClass());
			return Optional.of("UNKNOWN");
			
		} catch (Exception e) {
			log.error("getCurrentAuditor 실행 중 오류 발생: {}", e.getMessage());
			return Optional.of("ERROR");
		}
	}
} 