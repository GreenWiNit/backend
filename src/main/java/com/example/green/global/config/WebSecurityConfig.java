package com.example.green.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 설정 클래스입니다.
 * - 경로별 세부 권한 설정을 @PreAuthorize 메타 어노테이션으로 위임합니다.
 * - 기본적인 보안 설정만 중앙에서 관리합니다.
 * - 도메인별 보안 정책은 각 컨트롤러에서 명시적으로 선언합니다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))
			.authorizeHttpRequests(auth -> auth
				// 정적 리소스는 항상 허용
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				// 개발/운영 도구 경로 허용
				.requestMatchers(
					"/h2-console/**",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-ui.html",
					"/swagger-resources/**",
					"/webjars/**",
					"/favicon.ico",
					"/actuator/health"
				).permitAll()
				// API 경로는 메소드 레벨 @PreAuthorize로 세밀하게 제어
				.requestMatchers("/api/**").permitAll()
				// 나머지 경로
				.anyRequest().permitAll()
			)
			// HTTP Basic 인증 활성화 (테스트용)
			.httpBasic(httpBasic -> httpBasic.realmName("Test Realm"));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration cfg = new CorsConfiguration();
		cfg.setAllowedOriginPatterns(List.of("*"));
		cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		cfg.setAllowedHeaders(List.of("*"));
		cfg.setAllowCredentials(true);
		cfg.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
		src.registerCorsConfiguration("/**", cfg);
		return src;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails mockUser = new PrincipalDetails(1L, "ROLE_USER");
		UserDetails mockAdmin = new PrincipalDetails(2L, "ROLE_ADMIN");
		return new InMemoryUserDetailsManager(mockUser, mockAdmin);
	}
}
