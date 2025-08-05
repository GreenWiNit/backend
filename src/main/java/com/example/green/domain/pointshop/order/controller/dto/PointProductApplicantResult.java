package com.example.green.domain.pointshop.order.controller.dto;

import java.time.LocalDateTime;

import com.example.green.domain.pointshop.order.entity.vo.OrderDeliveryStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 상품 교환 신청자 결과")
public record PointProductApplicantResult(
	@Schema(description = "사용자 키", example = "oauth_number")
	String memberKey,
	@Schema(description = "사용자 이메일", example = "greenwinit@gmail.com")
	String memberEmail,
	@Schema(description = "교환 신청 일자")
	LocalDateTime exchangedAt,
	@Schema(description = "배송 상태")
	OrderDeliveryStatus deliveryStatus
) {
}
