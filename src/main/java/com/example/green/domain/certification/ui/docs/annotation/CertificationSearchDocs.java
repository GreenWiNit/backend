package com.example.green.domain.certification.ui.docs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(
	responseCode = "200",
	description = """
		성공 challenge type에 따라 다른 응답이 노출됩니다.
		* "P" Type - Group Code 미존재
		* "T" Type - Group Code 존재
		""",
	content = @Content(
		schema = @Schema(
			example = """
				{
				  "data": {
				    "totalElements": 2,
				    "totalPages": 1,
				    "currentPage": 0,
				    "pageSize": 20,
				    "hasNext": false,
				    "content": [
				      {
				        "id": 1,
				        "challenge": {
				          "id": 123,
				          "name": "개인 챌린지",
				          "type": "P"
				        },
				        "status": "PENDING"
				      },
				      {
				        "id": 2,
				        "challenge": {
				          "id": 124,
				          "name": "팀 챌린지",
				          "type": "T",
				          "groupCode": "T-20250819-001"
				        },
				        "status": "APPROVED"
				      }
				    ]
				  }
				}
				"""
		)
	)
)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CertificationSearchDocs {
}
