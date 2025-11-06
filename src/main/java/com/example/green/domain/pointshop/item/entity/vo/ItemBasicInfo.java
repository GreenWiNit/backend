package com.example.green.domain.pointshop.item.entity.vo;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.pointshop.item.exception.PointItemException;

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
public class ItemBasicInfo {

	private static final int ITEM_NAME_MIN_LENGTH = 2;
	private static final int ITEM_NAME_MAX_LENGTH = 15;
	private static final int ITEM_DESCRIPTION_MAX_LENGTH = 100;

	@Column(nullable = false, name = "item_name")
	private String itemName;

	@Column(nullable = false)
	private String description;

	public ItemBasicInfo(String itemName, String description) {
		validateNullCheck(itemName, description);
		String trimmedItemName = itemName.trim();
		String trimmedDescription = description.trim();

		validateBusiness(trimmedItemName, trimmedDescription);
		this.itemName = trimmedItemName;
		this.description = trimmedDescription;
	}

	private static void validateNullCheck(String itemName, String description) {
		validateNullData(itemName, REQUIRED_ITEM_NAME);
		validateNullData(description, REQUIRED_ITEM_DESCRIPTION);
	}

	private void validateBusiness(String name, String description) {
		itemValidateName(name);
		validateDescription(description);
	}

	private static void itemValidateName(String name) {
		int length = name.length();
		if (length < ITEM_NAME_MIN_LENGTH || length > ITEM_NAME_MAX_LENGTH) {
			throw new PointItemException(INVALID_ITEM_NAME);
		}

	}

	private static void validateDescription(String description) {
		if (description.length() > ITEM_DESCRIPTION_MAX_LENGTH) {
			throw new PointItemException(INVALID_ITEM_DESCRIPTION);
		}
	}
}
