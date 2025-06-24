package com.example.green.domain.pointshop.entity.vo;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

public record BasicInfo(String code, String name, String description) {

	private static final String POINT_CODE_REGEX = "^PRD-[A-Z]{2}-\\d{3}$";
	private static final Pattern POINT_CODE_PATTERN = Pattern.compile(POINT_CODE_REGEX);
	private static final int POINT_NAME_MIN_LENGTH = 2;
	private static final int POINT_NAME_MAX_LENGTH = 15;
	private static final int POINT_DESCRIPTION_MAX_LENGTH = 100;

	public BasicInfo {
		validateNullCheck(code, name, description);

		code = code.trim();
		name = name.trim();
		description = description.trim();

		validate(code, name, description);
	}

	private static void validateNullCheck(String code, String name, String description) {
		if (code == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_CODE);
		}
		if (name == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_NAME);
		}
		if (description == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_DESCRIPTION);
		}
	}

	private static void validate(String code, String name, String description) {
		if (!POINT_CODE_PATTERN.matcher(code).matches()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_CODE);
		}
		if (name.length() < POINT_NAME_MIN_LENGTH || name.length() > POINT_NAME_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_NAME);
		}
		if (description.length() > POINT_DESCRIPTION_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_DESCRIPTION);
		}
	}
}