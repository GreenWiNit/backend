package com.example.green.domain.pointshop.product.entity.vo;

import static com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

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
public class Code {

	private static final String POINT_CODE_REGEX = "^PRD-[A-Z]{2}-\\d{3}$";
	private static final Pattern POINT_CODE_PATTERN = Pattern.compile(POINT_CODE_REGEX);

	@Column(nullable = false)
	private String code;

	public Code(String code) {
		validateNullData(code, REQUIRED_CODE);
		String trimmedCode = code.trim().toUpperCase();
		validateCode(trimmedCode);
		this.code = trimmedCode;
	}

	private static void validateCode(String code) {
		if (!POINT_CODE_PATTERN.matcher(code.trim()).matches()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_CODE);
		}
	}
}
