package com.example.green.domain.pointshop.delivery.entity.vo;

import static com.example.green.domain.pointshop.delivery.exception.DeliveryAddressExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.delivery.exception.DeliveryAddressException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

	public static Address of(String roadAddress, String detailAddress, String zipCode) {
		validateEmptyString(roadAddress, "도로명 주소는 필수 값 입니다.");
		validateEmptyString(detailAddress, "상세 주소는 필수 값 입니다.");
		validateEmptyString(zipCode, "우편 번호는 필수 값 입니다.");
		if (!ZIP_CODE_PATTERN.matcher(zipCode).matches()) {
			throw new DeliveryAddressException(INVALID_ZIP_CODE);
		}
		return new Address(roadAddress, detailAddress, zipCode);
	}
}
