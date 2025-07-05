package com.example.green.domain.auth.entity.verification.vo;

import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Attempt {

	private static final int MAX_TRY_COUNT = 5;

	private Integer attempts;

	private Attempt(Integer attempts) {
		if (MAX_TRY_COUNT < attempts) {
			throw new AuthException(PhoneExceptionMessage.OVER_MAX_TRY);
		}
		this.attempts = attempts;
	}

	public static Attempt init() {
		return new Attempt(0);
	}

	public Attempt increaseCount() {
		return new Attempt(this.attempts + 1);
	}
}
