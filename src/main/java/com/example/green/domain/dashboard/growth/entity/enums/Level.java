package com.example.green.domain.dashboard.growth.entity.enums;

import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;

import lombok.Getter;

@Getter
public enum Level {
	SOIL("흙"),
	SPROUT("새싹"),
	SAPLING("묘목"),
	TREE("나무");

	private final String displayName;

	Level(String displayName) {
		this.displayName = displayName;
	}

	public static Level fromDisplayName(String displayName) {
		for (Level l : values()) {
			if (l.displayName.equals(displayName)) {
				return l;
			}
		}
		throw new GrowthException(GrowthExceptionMessage.WRONG_LEVEL_NAME);
	}
}


