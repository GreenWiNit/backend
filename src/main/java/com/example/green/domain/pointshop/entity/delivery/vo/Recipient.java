package com.example.green.domain.pointshop.entity.delivery.vo;

import static com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressException;

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
public class Recipient {

	private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

	@Column(nullable = false)
	private String recipientName;
	@Column(nullable = false)
	private String phoneNumber;

	public static Recipient of(String recipientName, String phoneNumber) {
		validateEmptyString(recipientName, "물품 수령자 이름은 필수 값 입니다.");
		validateEmptyString(phoneNumber, "물품 수령자 전화 번호는 필수 값 입니다.");
		if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
			throw new DeliveryAddressException(INVALID_PHONE_NUMBER);
		}
		return new Recipient(recipientName, phoneNumber);
	}
}
