package com.example.green.domain.pointshop.entity.order.vo;

import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryAddressSnapshot {

	@Column(nullable = false)
	private Long deliveryAddressId;
	@Column(nullable = false)
	private String recipientName;
	@Column(nullable = false)
	private String phoneNumber;
	@Column(nullable = false)
	private String roadAddress;
	@Column(nullable = false)
	private String detailAddress;
	@Column(nullable = false)
	private String zipCode;

	public static DeliveryAddressSnapshot of(
		Long deliveryAddressId,
		String recipientName,
		String phoneNumber,
		String roadAddress,
		String detailAddress,
		String zipCode
	) {
		validateAutoIncrementId(deliveryAddressId, "배송지 ID는 필수 값 입니다.");
		validateEmptyString(recipientName, "수령자 정보는 필수 값 입니다.");
		validateEmptyString(phoneNumber, "배송자 전화번호 정보는 필수 값 입니다.");
		validateEmptyString(roadAddress, "도로명 주소는 필수 값 입니다.");
		validateEmptyString(detailAddress, "상세 주소는 필수 값 입니다.");
		validateEmptyString(zipCode, "우편 번호는 필수 값 입니다.");
		return new DeliveryAddressSnapshot(
			deliveryAddressId, recipientName, phoneNumber, roadAddress, detailAddress, zipCode
		);
	}
}
