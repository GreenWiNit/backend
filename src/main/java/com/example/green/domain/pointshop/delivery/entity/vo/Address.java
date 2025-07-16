package com.example.green.domain.pointshop.delivery.entity.vo;

import static com.example.green.domain.pointshop.delivery.exception.DeliveryAddressExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.delivery.exception.DeliveryAddressException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address {

	private static final String ZIP_CODE_REGEX = "^\\d{5}$";
	private static final Pattern ZIP_CODE_PATTERN = Pattern.compile(ZIP_CODE_REGEX);

	@Column(nullable = false)
	private String roadAddress;
	@Column(nullable = false)
	private String detailAddress;
	@Column(nullable = false)
	private String zipCode;

	private Address(String roadAddress, String detailAddress, String zipCode) {
		validateEmptyString(roadAddress, REQUIRE_ROAD_ADDRESS);
		validateEmptyString(detailAddress, REQUIRE_DETAIL_ADDRESS);
		validateEmptyString(zipCode, REQUIRE_ZIP_CODE);
		if (!ZIP_CODE_PATTERN.matcher(zipCode).matches()) {
			throw new DeliveryAddressException(INVALID_ZIP_CODE);
		}

		this.roadAddress = roadAddress;
		this.detailAddress = detailAddress;
		this.zipCode = zipCode;
	}

	public static Address of(String roadAddress, String detailAddress, String zipCode) {
		return new Address(roadAddress, detailAddress, zipCode);
	}
}
