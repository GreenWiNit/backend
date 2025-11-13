package com.example.green.domain.dashboard.growth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePositionGrowthItemResponse(
	@Schema(description = "아이템 이름", example = "맑은 뭉개 구름")
	String itemName,
	@Schema(description = "아이템 사진", example = "https://my-plant-growth-bucket.s3.ap-northeast-2.amazonaws.com/images/sunflower_growth_1.jpg")
	String itemImgUrl,
	@Schema(description = "아이템 적용여부")
	boolean applicability,
	@Schema(description = "아이템 X 좌표 위치", example = "150.5")
	double positionX,
	@Schema(description = "아이템 Y 좌표 위치", example = "300.0")
	double positionY
) {
}
