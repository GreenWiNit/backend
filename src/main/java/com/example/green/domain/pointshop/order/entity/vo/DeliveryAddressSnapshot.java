package com.example.green.domain.pointshop.order.entity.vo;

import static com.example.green.domain.pointshop.order.exception.OrderExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddressSnapshot {

	private static final String FULL_ADDRESS_FORMAT = "%s (%s), %s";

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

	private DeliveryAddressSnapshot(
		Long id, String name, String phoneNumber, String road, String detail, String zipCode
	) {
		validateConstruction(id, name, phoneNumber, road, detail, zipCode);
		this.deliveryAddressId = id;
		this.recipientName = name;
		this.phoneNumber = phoneNumber;
		this.roadAddress = road;
		this.detailAddress = detail;
		this.zipCode = zipCode;
	}

	private static void validateConstruction(
		Long id, String name, String phoneNumber, String road, String detail, String zipCode
	) {
		validateAutoIncrementId(id, REQUIRE_DELIVERY_ADDRESS_ID);
		validateEmptyString(name, REQUIRE_RECIPIENT_NAME);
		validateEmptyString(phoneNumber, REQUIRE_RECIPIENT_PHONE_NUMBER);
		validateEmptyString(road, REQUIRE_ROAD_ADDRESS);
		validateEmptyString(detail, REQUIRE_DETAIL_ADDRESS);
		validateEmptyString(zipCode, REQUIRE_ZIP_CODE);
	}

	public static DeliveryAddressSnapshot of(
		Long id, String name, String phoneNumber, String road, String detail, String zipCode
	) {
		return new DeliveryAddressSnapshot(id, name, phoneNumber, road, detail, zipCode);
	}

	public String getFullAddress() {
		return String.format(FULL_ADDRESS_FORMAT, roadAddress, zipCode, detailAddress);
	}
}
