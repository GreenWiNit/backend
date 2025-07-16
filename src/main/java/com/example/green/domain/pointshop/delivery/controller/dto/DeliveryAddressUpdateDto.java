package com.example.green.domain.pointshop.delivery.controller.dto;

import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "배송지 수정 요청")
public record DeliveryAddressUpdateDto(
	@NotBlank(message = "수령자 이름은 필수 정보입니다.")
	@Schema(description = "배송 수령자 이름", example = "홍길동")
	String recipientName,
	@NotBlank(message = "수령자 전화번호는 필수 정보 입니다.")
	@Schema(description = "배송 수령자 전화번호", example = "010-1234-5678")
	String phoneNumber,
	@NotBlank(message = "배송지 도로명 주소는 필수 정보입니다.")
	@Schema(description = "도로명 주소", example = "OO시 OO구 OO로 000")
	String roadAddress,
	@NotBlank(message = "배송지 상세 주소는 필수 정보입니다.")
	@Schema(description = "상세 주소", example = "1층 로비")
	String detailAddress,
	@NotBlank(message = "배송지 우편 번호는 필수 정보입니다.")
	@Schema(description = "우편 번호", example = "12345")
	String zipCode
) {

	public Recipient toRecipient() {
		return Recipient.of(recipientName, phoneNumber);
	}

	public Address toAddress() {
		return Address.of(roadAddress, detailAddress, zipCode);
	}
}
