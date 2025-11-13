package com.example.green.domain.dashboard.growth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이템 위치 조정 요청")
public record ChangePositionRequest(
	@Schema(description = "아이템 X 좌표 위치", example = "150.5")
	double positionX,
	@Schema(description = "아이템 Y 좌표 위치", example = "300.0")
	double positionY

) {
}
