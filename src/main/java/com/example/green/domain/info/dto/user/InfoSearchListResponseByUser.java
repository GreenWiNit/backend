package com.example.green.domain.info.dto.user;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[사용자단] 정보 공유 목록 조회 응답 DTO")
public record InfoSearchListResponseByUser(
	@ArraySchema(
		schema = @Schema(
			description = "정보 공유 단일 응답 객체 리스트",
			implementation = InfoSearchResponseByUser.class
		),
		arraySchema = @Schema(description = "content 배열")
	)
	List<InfoSearchResponseByUser> content
) {
}
