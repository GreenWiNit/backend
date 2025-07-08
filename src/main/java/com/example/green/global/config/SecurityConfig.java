package com.example.green.global.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.green.domain.auth.CustomSuccessHandler;
import com.example.green.domain.auth.OAuth2FailureHandler;
import com.example.green.domain.auth.filter.JwtFilter;
import com.example.green.domain.auth.service.CustomOAuth2UserService;
import com.example.green.domain.auth.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Spring Security 설정 클래스입니다.
 * - OAuth2 로그인과 JWT 토큰 기반 인증을 지원합니다.
 * - 경로별 세부 권한 설정을 @PreAuthorize 메타 어노테이션으로 위임합니다.
 * - 기본적인 보안 설정만 중앙에서 관리합니다.
 * - 도메인별 보안 정책은 각 컨트롤러에서 명시적으로 선언합니다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 활성화

public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final OAuth2FailureHandler oauth2FailureHandler;
	private final TokenService tokenService;
	private final String frontendBaseUrl;
	private final String backendBaseUrl;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
		OAuth2FailureHandler oauth2FailureHandler, TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl,
		@Value("${app.backend.base-url}") String backendBaseUrl) {

		this.customOAuth2UserService = customOAuth2UserService;
		this.customSuccessHandler = customSuccessHandler;
		this.oauth2FailureHandler = oauth2FailureHandler;
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
		this.backendBaseUrl = backendBaseUrl;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF disable (JWT의 stateless한 상태로 관리할 것이기 때문)
		http
			.csrf(AbstractHttpConfigurer::disable);

		// From 로그인 방식 disable
		http
			.formLogin(AbstractHttpConfigurer::disable);

		// HTTP Basic 인증 방식 disable
		http
			.httpBasic(AbstractHttpConfigurer::disable);

		// CORS 설정 (Spring Security 레벨)
		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowedOrigins(Arrays.asList(frontendBaseUrl, backendBaseUrl));
					configuration.setAllowedMethods(Collections.singletonList("*"));
					configuration.setAllowCredentials(true);
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setMaxAge(3600L);

					// 두 헤더를 함께 설정 (덮어쓰기 방지)
					configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

					return configuration;
				}
			}));

		//JWTFilter 추가 (AccessToken 검증용)
		http
			.addFilterBefore(new JwtFilter(tokenService), UsernamePasswordAuthenticationFilter.class);

		// OAuth2 로그인 설정
		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)
				.failureHandler(oauth2FailureHandler));

		// 경로별 인가 작업
		http
			.authorizeHttpRequests(auth -> auth
				// 정적 리소스는 항상 허용
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				// 개발/운영 도구 경로 허용
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-ui.html",
					"/swagger-resources/**",
					"/webjars/**",
					"/favicon.ico",
					"/actuator/health"
				).permitAll()
				// OAuth2 로그인 및 Auth API 허용
				.requestMatchers("/oauth2/**", "/login/**", "/api/auth/**").permitAll()
				// 테스트 페이지 허용
				.requestMatchers("/oauth-test.html", "/signup.html").permitAll()
				// 루트 경로 허용
				.requestMatchers("/").permitAll()
				// 나머지 모든 요청은 메타 어노테이션(@PreAuthorize)으로 권한 검사
				.anyRequest().permitAll());

		// 세션 설정: STATELESS (JWT 토큰 기반 인증을 위해)
		http
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}


