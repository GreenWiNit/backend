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
public class Recipient {

	private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

	@Column(nullable = false)
	private String recipientName;
	@Column(nullable = false)
	private String phoneNumber;

	private Recipient(String recipientName, String phoneNumber) {
		validateEmptyString(recipientName, REQUIRE_RECIPIENT_NAME);
		validateEmptyString(phoneNumber, REQUIRE_RECIPIENT_PHONE_NUMBER);
		if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
			throw new DeliveryAddressException(INVALID_PHONE_NUMBER);
		}
		this.recipientName = recipientName;
		this.phoneNumber = phoneNumber;
	}

	public static Recipient of(String recipientName, String phoneNumber) {
		return new Recipient(recipientName, phoneNumber);
	}
}
