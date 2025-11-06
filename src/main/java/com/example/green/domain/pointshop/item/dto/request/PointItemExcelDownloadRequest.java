package com.example.green.domain.pointshop.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "아이템 목록 엑셀 다운로드 요청")
public record PointItemExcelDownloadRequest(
	@Schema(description = "아이템 코드 및 아이템 명", example = "맑은 뭉게 구름")
	@Size(min = 2)
	String keyword
) {

}
