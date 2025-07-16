package com.example.green.domain.pointshop.product.entity.vo;

import static com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage;

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

	private static final int POINT_NAME_MIN_LENGTH = 2;
	private static final int POINT_NAME_MAX_LENGTH = 15;
	private static final int POINT_DESCRIPTION_MAX_LENGTH = 100;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;

	public BasicInfo(String name, String description) {
		validateNullCheck(name, description);
		String trimmedName = name.trim();
		String trimmedDescription = description.trim();

		validateBusiness(trimmedName, trimmedDescription);
		this.name = trimmedName;
		this.description = trimmedDescription;
	}

	private static void validateNullCheck(String name, String description) {
		validateNullData(name, REQUIRED_NAME);
		validateNullData(description, REQUIRED_DESCRIPTION);
	}

	private void validateBusiness(String name, String description) {
		validateName(name);
		validateDescription(description);
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
