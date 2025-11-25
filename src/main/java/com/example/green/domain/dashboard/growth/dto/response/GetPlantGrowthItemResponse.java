package com.example.green.domain.dashboard.growth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "식물 성장 대시보드 아이템 조회")
public record GetPlantGrowthItemResponse(

	@Schema(description = "아이템 ID", example = "1")
	Long id,
	@Schema(description = "아이템 이름", example = "맑은 뭉개 구름")
	String itemName,
	@Schema(description = "아이템 사진", example = "https://my-plant-growth-bucket.s3.ap-northeast-2.amazonaws.com/images/sunflower_growth_1.jpg")
	String itemImgUrl,
	@Schema(description = "아이템 적용여부")
	boolean applicability,
	@Schema(description = "아이템 X좌표")
	double positionX,
	@Schema(description = "아이템 y좌표")
	double positionY
) {
}
