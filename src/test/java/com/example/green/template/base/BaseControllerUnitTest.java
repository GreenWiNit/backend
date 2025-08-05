package com.example.green.template.base;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.green.global.security.PrincipalDetails;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@Import(BaseControllerUnitTest.TestWebSecurityConfig.class)
public class BaseControllerUnitTest {

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}

	@TestConfiguration
	static class TestWebSecurityConfig implements WebMvcConfigurer {
		@Bean
		@Primary
		public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
			return http
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.csrf(AbstractHttpConfigurer::disable)
				.build();
		}

		// 테스트용 임시 resolver 추가
		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
			resolvers.add(new HandlerMethodArgumentResolver() {
				@Override
				public boolean supportsParameter(MethodParameter parameter) {
					return parameter.hasParameterAnnotation(AuthenticationPrincipal.class) &&
						parameter.getParameterType().equals(PrincipalDetails.class);
				}

				@Override
				public Object resolveArgument(
					MethodParameter parameter,
					ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest,
					WebDataBinderFactory binderFactory
				) {

					PrincipalDetails mockPrincipal = mock(PrincipalDetails.class);
					when(mockPrincipal.getMemberId()).thenReturn(1L);
					when(mockPrincipal.getMemberKey()).thenReturn("testMemberKey");
					when(mockPrincipal.getEmail()).thenReturn("test@example.com");
					return mockPrincipal;
				}
			});
		}
	}
}
