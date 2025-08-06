package com.example.green.domain.pointshop.order.controller.dto;

import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "상품 교환 주문 요청")
public record SingleOrderRequest(
	@NotNull(message = "상품 배송지 ID 정보는 필수 값 입니다.")
	@Schema(description = "배송지 ID")
	Long deliveryAddressId,
	@NotNull(message = "교환 상품 ID는 필수 값 입니다.")
	@Schema(description = "교환 상품 ID")
	Long orderItemId,
	@NotNull(message = "상품 교환 수량은 필수 값 입니다.")
	@Schema(description = "교환 수량")
	Integer quantity
) {

	public SingleOrderCommand toCommand(Long memberId, String memberKey, String memberEmail) {
		return new SingleOrderCommand(
			new MemberSnapshot(memberId, memberKey, memberEmail),
			deliveryAddressId,
			orderItemId,
			quantity
		);
	}
}
