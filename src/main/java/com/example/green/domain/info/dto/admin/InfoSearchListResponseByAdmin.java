package com.example.green.domain.info.dto.admin;

import java.util.List;

import com.example.green.domain.info.dto.InfoPage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[관리자단] 정보 공유 목록 조회 응답 DTO")
public record InfoSearchListResponseByAdmin(

	@ArraySchema(
		schema = @Schema(
			description = "[관리자단] 정보 공유 단일 응답 객체 리스트",
			implementation = InfoSearchResponseByAdmin.class
		),
		arraySchema = @Schema(description = "content 배열")
	)
	List<InfoSearchResponseByAdmin> content,

	@Schema(
		description = "페이지 정보",
		implementation = InfoPage.class
	)
	InfoPage page

) {
}
