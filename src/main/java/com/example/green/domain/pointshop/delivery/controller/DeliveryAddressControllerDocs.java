package com.example.green.domain.pointshop.delivery.controller;

import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.delivery.service.result.DeliveryResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "배송지 API", description = "배송지 관련 API 모음 입니다.")
public interface DeliveryAddressControllerDocs {

	@Operation(summary = "단일 배송지 저장", description = "단일 배송지를 저장합니다.")
	@ApiResponse(responseCode = "200", description = "배송지 정보 추가에 성공했습니다.")
	@ApiResponse(
		responseCode = "400", description = "이미 배송지 정보가 존재합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> createDeliveryAddress(DeliveryAddressCreateDto dto);

	@Operation(summary = "단일 배송지 조회", description = "단일 배송지를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "배송지 조회에 성공했습니다.")
	@ApiResponse(
		responseCode = "404", description = "배송지 정보를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<DeliveryResult> getDeliveryAddress();

	@Operation(summary = "단일 배송지 수정", description = "단일 배송지를 수정합니다.")
	@ApiResponse(responseCode = "200", description = "배송지 수정에 성공했습니다.")
	@ApiResponse(
		responseCode = "404", description = "배송지 정보를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent updateDeliveryAddress(DeliveryAddressUpdateDto dto, Long deliveryAddressId);
}
