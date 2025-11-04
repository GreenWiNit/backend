package com.example.green.domain.pointshop.item.entity.vo;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.util.regex.Pattern;

import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.product.exception.PointProductException;

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
public class ItemCode {

	private static final String ITEM_CODE_REGEX = "^ITM-[A-Z]{2}-\\d{3}$";
	private static final Pattern ITEM_CODE_PATTERN = Pattern.compile(ITEM_CODE_REGEX);

	@Column(nullable = false)
	private String code;

	public ItemCode(String code) {
		validateNullData(code, REQUIRED_ITEM_CODE);
		String trimmedCode = code.trim().toUpperCase();
		validateCode(trimmedCode);
		this.code = trimmedCode;
	}

	private static void validateCode(String code) {
		if (!ITEM_CODE_PATTERN.matcher(code.trim()).matches()) {
			throw new PointProductException(PointItemExceptionMessage.INVALID_ITEM_CODE);
		}
	}
}
