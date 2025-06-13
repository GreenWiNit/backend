package com.example.green.global.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))
				.authorizeHttpRequests(auth -> auth
						// 1) 스프링 내장 정적 리소스
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
						// 2) 추가 공개 경로
						.requestMatchers(SecurityPathConfig.PUBLIC_STATIC_PATHS).permitAll()
						// 3) 공개 GET API
						.requestMatchers(HttpMethod.GET, SecurityPathConfig.PUBLIC_GET_PATHS).permitAll()
						// 4) 회원 API (GET/POST 모두 공개)
						.requestMatchers(HttpMethod.GET,  SecurityPathConfig.USER_API_PATH).permitAll()
						.requestMatchers(HttpMethod.POST, SecurityPathConfig.USER_API_PATH).permitAll()
						// 5) Green API (조회만 공개)
						.requestMatchers(HttpMethod.GET, SecurityPathConfig.GREEN_API_PATH).permitAll()
						// 6) (dev profile에서만 활성화) v1 API
						.requestMatchers(SecurityPathConfig.DEV_API_PATH).permitAll()
						// 7) 그 외는 인증 필요
						.anyRequest().authenticated()
				);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration cfg = new CorsConfiguration();
		cfg.setAllowedOriginPatterns(List.of("*"));
		cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
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
		return new InMemoryUserDetailsManager(mockUser);
	}
}