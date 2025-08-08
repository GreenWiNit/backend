package com.example.green.global.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.green.domain.auth.CustomSuccessHandler;
import com.example.green.domain.auth.OAuth2FailureHandler;
import com.example.green.domain.auth.filter.JwtFilter;
import com.example.green.domain.auth.service.CustomOAuth2UserService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.resolver.CustomAuthorizationRequestResolver;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * 1. OAuth2/정적 리소스용 FilterChain (JWT 필터 없음)
 * 2. API용 FilterChain (JWT 필터 있음, @PreAuthorize로 세부 권한 제어)
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 활성화
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final OAuth2FailureHandler oauth2FailureHandler;
	private final TokenService tokenService;
	private final AllowedDomainsPolicy allowedDomainsPolicy;
	private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
		OAuth2FailureHandler oauth2FailureHandler, TokenService tokenService,
		AllowedDomainsPolicy allowedDomainsPolicy, CustomAuthorizationRequestResolver customAuthorizationRequestResolver) {

		this.customOAuth2UserService = customOAuth2UserService;
		this.customSuccessHandler = customSuccessHandler;
		this.oauth2FailureHandler = oauth2FailureHandler;
		this.tokenService = tokenService;
		this.allowedDomainsPolicy = allowedDomainsPolicy;
		this.customAuthorizationRequestResolver = customAuthorizationRequestResolver;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * OAuth2 및 정적 리소스용 SecurityFilterChain
	 * - JWT 필터를 적용하지 않음
	 * - OAuth2 로그인 플로우 처리
	 * - 정적 리소스 및 개발 도구 접근 허용
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain oauthAndStaticResourcesSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
			// 특정 경로에만 이 필터체인 적용
			.securityMatcher(
				"/oauth2/**",
				"/login/**",
				"/swagger-ui/**",
				"/v3/api-docs/**",
				"/swagger-ui.html",
				"/swagger-resources/**",
				"/webjars/**",
				"/favicon.ico",
				"/actuator/health",
				"/"
			)
			// CSRF disable
			.csrf(AbstractHttpConfigurer::disable)
			// Form 로그인 방식 disable  
			.formLogin(AbstractHttpConfigurer::disable)
			// HTTP Basic 인증 방식 disable
			.httpBasic(AbstractHttpConfigurer::disable)
			// CORS 설정
			.cors(corsCustomizer -> corsCustomizer.configurationSource(createCorsConfigurationSource()))
			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.authorizationRequestResolver(customAuthorizationRequestResolver))
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)
				.failureHandler(oauth2FailureHandler))
			// 모든 요청 허용 (OAuth2 플로우와 정적 리소스)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.anyRequest().permitAll())
			// 세션 설정: OAuth2에서는 세션 사용
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.sessionConcurrency(concurrency -> concurrency
					.maximumSessions(1)
					.maxSessionsPreventsLogin(false))
				.sessionFixation().migrateSession())
			.build();
	}

	/**
	 * API용 SecurityFilterChain  
	 * - JWT 필터 적용
	 * - @PreAuthorize로 세부 권한 제어
	 * - Stateless 세션 정책
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
			// API 경로에만 이 필터체인 적용
			.securityMatcher("/api/**")
			// CSRF disable (JWT의 stateless한 상태로 관리)
			.csrf(AbstractHttpConfigurer::disable)
			// Form 로그인 방식 disable
			.formLogin(AbstractHttpConfigurer::disable)
			// HTTP Basic 인증 방식 disable
			.httpBasic(AbstractHttpConfigurer::disable)
			// CORS 설정
			.cors(corsCustomizer -> corsCustomizer.configurationSource(createCorsConfigurationSource()))
			// JWT 필터 추가 (AccessToken 검증용)
			.addFilterBefore(new JwtFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
			// 모든 API 요청은 @PreAuthorize에서 권한 검사
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
			// 세션 설정: STATELESS (JWT 토큰 기반 인증)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.build();
	}

	/**
	 * CORS 설정을 별도 메서드로 분리하여 두 FilterChain에서 공통 사용
	 */
	private CorsConfigurationSource createCorsConfigurationSource() {
		return new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(allowedDomainsPolicy.getAllAllowedOrigins());

				// 와일드카드 패턴 도메인들은 setAllowedOriginPatterns로 분리해야한다.
				configuration.setAllowedOriginPatterns(allowedDomainsPolicy.getAllowedOriginPatterns());
				configuration.setAllowedMethods(
					Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
				configuration.setAllowedHeaders(Arrays.asList(
					"Authorization",
					"Content-Type",
					"X-Requested-With",
					"Accept",
					"Origin",
					"Access-Control-Request-Method",
					"Access-Control-Request-Headers",
					"X-XSRF-TOKEN"
				));
				configuration.setAllowCredentials(true);
				configuration.setMaxAge(3600L);

				// 두 헤더를 함께 설정 (덮어쓰기 방지)
				configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization", "Content-Disposition"));

				return configuration;
			}
		};
	}
}


