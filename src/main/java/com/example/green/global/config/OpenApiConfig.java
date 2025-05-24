package com.example.green.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Swagger UI에서 Bearer Token 입력을 가능하게 하는 설정입니다.
 * 상단의 Authorize 버튼 클릭 시 토큰을 입력하면 이후 모든 요청에 Authorization 헤더가 포함됩니다.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "Bearer Token";

		return new OpenAPI()
			.info(new Info()
				.title("Green API 문서")
				.version("v1.0")
				.description("Green 프로젝트의 Swagger 문서입니다.\n\n" +
					"\uD83D\uDD10 토큰 인증이 필요한 API는 상단의 Authorize 버튼을 클릭한 뒤, `토큰`만 입력해주세요.\n"
					+ "\nBearer는 자동으로 붙으므로, eyJhbGciOi... 와 같은 순수 토큰 문자열만 입력하시면 됩니다.")
			)
			.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
			.components(new Components()
				.addSecuritySchemes(securitySchemeName,
					new SecurityScheme()
						.name(securitySchemeName)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER)
				)
			);
	}
}