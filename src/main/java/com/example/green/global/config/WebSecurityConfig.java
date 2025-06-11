package com.example.green.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

	// TODO: JWT 및 OAuth2 관련 필터들이 구현되면 주입받을 예정

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> headers
				.frameOptions(frame -> frame.disable()) // H2 Console용
			)
			.authorizeHttpRequests(auth -> auth
				// 정적 리소스 및 공개 경로
				.requestMatchers(SecurityPathConfig.PUBLIC_STATIC_PATHS).permitAll()
				
				// GET 요청만 허용하는 경로들
				.requestMatchers(SecurityPathConfig.PUBLIC_GET_PATHS).permitAll()
				
				// 사용자 API (GET, POST만 허용)
				.requestMatchers(HttpMethod.GET, SecurityPathConfig.USER_API_PATH).permitAll()
				.requestMatchers(HttpMethod.POST, SecurityPathConfig.USER_API_PATH).permitAll()
				
				// Green API (GET만 허용)
				.requestMatchers(HttpMethod.GET, SecurityPathConfig.GREEN_API_PATH).permitAll()

				
				// TODO: 개발 과정에서 현재 v1 API 모든 요청을 허용하고 있음. 추후 권한 관리 필요
				.requestMatchers("/v1/**").permitAll()
				
				// 나머지는 인증 필요
				.anyRequest().authenticated()
			);
			
		// TODO: OAuth2 로그인 및 JWT 필터 설정 (추후 구현)

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 개발 환경에서는 모든 오리진 허용
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		
		// 프로덕션 환경에서는 아래 주석을 해제하고 특정 오리진만 허용
		/*
		configuration.setAllowedOrigins(Arrays.asList(
			"http://localhost:8080",
			"https://localhost:8080",
			"http://localhost:3000",
			"https://localhost:3000",
			"https://www.greenwinit.store"
		));
		*/

		List<String> allowedMethods = Arrays.asList(
			HttpMethod.GET.name(), 
			HttpMethod.POST.name(), 
			HttpMethod.PUT.name(), 
			HttpMethod.DELETE.name(), 
			HttpMethod.OPTIONS.name(), 
			HttpMethod.PATCH.name()
		);

		configuration.setAllowedMethods(allowedMethods);
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		// OIDC 구현 전까지 임시로 사용할 유저
		UserDetails mockUser = User.builder()
				.username("mockUser")
				.password(passwordEncoder().encode("mockPassword"))
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(mockUser);
	}
}