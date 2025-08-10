package com.example.green.domain.challenge.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.green.global.utils.UlidUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

	public static String generateTeamGroupCode(LocalDateTime now) {
		String dateCode = now.format(DATE_FORMATTER);
		String timeCode = now.format(TIME_FORMATTER);
		String ulidSuffix = UlidUtils.generate().substring(22);  // 뒷 4자리

		return String.format("T-%s-%s-%s",
			dateCode,
			timeCode,
			ulidSuffix);
	}

	public static String generatePersonalCode(LocalDateTime now, long lastId) {
		String dateCode = now.format(DATE_FORMATTER);
		return String.format("CH-P-%s-%03d", dateCode, lastId);
	}

	public static String generateTeamCode(LocalDateTime now, long lastId) {
		String dateCode = now.format(DATE_FORMATTER);
		return String.format("CH-T-%s-%03d", dateCode, lastId);
	}
}
