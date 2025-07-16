package com.example.green.domain.pointshop.delivery.service.result;

import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배송지 조회 결과")
public record DeliveryResult(
	@Schema(description = "배송지 식별자", example = "1")
	Long deliveryAddressId,
	@Schema(description = "배송 수령자 이름", example = "홍길동")
	String recipientName,
	@Schema(description = "배송 수령자 전화번호", example = "010-1234-5678")
	String phoneNumber,
	@Schema(description = "배송지 도로명 주소", example = "00시 00구 00로 11")
	String roadAddress,
	@Schema(description = "배송지 상세 주소", example = "1층")
	String detailAddress,
	@Schema(description = "배송지 우편번호", example = "12345")
	String zipCode
) {

	public static DeliveryResult of(Long deliveryAddressId, Recipient recipient, Address address) {
		return new DeliveryResult(
			deliveryAddressId,
			recipient.getRecipientName(),
			recipient.getPhoneNumber(),
			address.getRoadAddress(),
			address.getDetailAddress(),
			address.getZipCode()
		);
	}
}
