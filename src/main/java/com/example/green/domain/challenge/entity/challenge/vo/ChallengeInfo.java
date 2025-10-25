package com.example.green.domain.challenge.entity.challenge.vo;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import com.example.green.domain.challenge.exception.ChallengeException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ChallengeInfo {

	private static final int MAX_NAME_LENGTH = 90;

	@Column(length = 30, nullable = false, unique = true)
	private String code;

	@Column(length = 90, nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer point;

	private ChallengeInfo(String code, String name, Integer point) {
		validateCode(code);
		validateName(name);
		validatePoint(point);

		this.code = code;
		this.name = name;
		this.point = point;
	}

	private void validateCode(String code) {
		if (code == null || code.isBlank()) {
			throw new ChallengeException(CHALLENGE_CODE_BLANK);
		}
	}

	public static ChallengeInfo of(String code, String name, Integer point) {
		return new ChallengeInfo(code, name, point);
	}

	private void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new ChallengeException(CHALLENGE_NAME_BLANK);
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new ChallengeException(CHALLENGE_NAME_LENGTH_EXCEEDED);
		}
	}

	private void validatePoint(Integer point) {
		if (point == null) {
			throw new ChallengeException(CHALLENGE_POINT_BLANK);
		}
		if (point < 0) {
			throw new ChallengeException(POINT_LESS_THAN_ZERO);
		}
	}
}
