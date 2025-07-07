package com.example.green.domain.auth.entity.verification.vo;

import static com.example.green.domain.auth.exception.PhoneExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.auth.exception.AuthException;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class PhoneNumber {

	private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

	private String number;

	private PhoneNumber(String number) {
		validateEmptyString(number, REQUIRES_PHONE_NUMBER);
		if (!PHONE_NUMBER_PATTERN.matcher(number).matches()) {
			throw new AuthException(INVALID_PHONE_NUMBER);
		}
		this.number = number;
	}

	public static PhoneNumber of(String number) {
		return new PhoneNumber(number);
	}
}
