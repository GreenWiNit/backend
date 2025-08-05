package com.example.integration.config;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.security.PrincipalDetails;

@TestConfiguration
public class TestTokenServiceConfig {

	@Bean
	@Primary
	public TokenService tokenService() {
		TokenService mock = mock(TokenService.class);
		when(mock.validateAccessToken(anyString())).thenReturn(true);
		PrincipalDetails principal = new PrincipalDetails(1L, "test-key", "ROLE_USER", "Test User", "test@test.com");
		when(mock.createAuthentication(anyString()))
			.thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
		return mock;
	}
}