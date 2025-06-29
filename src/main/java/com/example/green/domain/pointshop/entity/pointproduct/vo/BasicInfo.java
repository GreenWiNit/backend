package com.example.green.domain.pointshop.entity.pointproduct.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class BasicInfo {

	@Column(nullable = false)
	private String code;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;

	private static final String POINT_CODE_REGEX = "^PRD-[A-Z]{2}-\\d{3}$";
	private static final Pattern POINT_CODE_PATTERN = Pattern.compile(POINT_CODE_REGEX);
	private static final int POINT_NAME_MIN_LENGTH = 2;
	private static final int POINT_NAME_MAX_LENGTH = 15;
	private static final int POINT_DESCRIPTION_MAX_LENGTH = 100;

	public BasicInfo(String code, String name, String description) {
		validateNullCheck(code, name, description);
		String trimmedCode = code.trim().toUpperCase();
		String trimmedName = name.trim();
		String trimmedDescription = description.trim();

		validateBusiness(trimmedCode, trimmedName, trimmedDescription);
		this.code = trimmedCode;
		this.name = trimmedName;
		this.description = trimmedDescription;
	}

	private static void validateNullCheck(String code, String name, String description) {
		validateNullData(code, REQUIRED_CODE);
		validateNullData(name, REQUIRED_NAME);
		validateNullData(description, REQUIRED_DESCRIPTION);
	}

	private void validateBusiness(String code, String name, String description) {
		validateCode(code);
		validateName(name);
		validateDescription(description);
	}

	private static void validateCode(String code) {
		if (!POINT_CODE_PATTERN.matcher(code.trim()).matches()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_CODE);
		}
	}

	private static void validateName(String name) {
		int length = name.length();
		if (length < POINT_NAME_MIN_LENGTH || length > POINT_NAME_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_NAME);
		}
	}

	private static void validateDescription(String description) {
		if (description.length() > POINT_DESCRIPTION_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_DESCRIPTION);
		}
	}
}