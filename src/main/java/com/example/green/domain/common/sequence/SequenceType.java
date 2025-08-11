package com.example.green.domain.common.sequence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum SequenceType {
	TEAM_CHALLENGE_GROUP("group", "T-%s-%03d"),
	PERSONAL_CHALLENGE("personal", "CH-P-%s-%03d"),
	TEAM_CHALLENGE("team", "CH-T-%s-%03d");

	private final String prefix;
	private final String codeFormat;

	SequenceType(String prefix, String codeFormat) {
		this.prefix = prefix;
		this.codeFormat = codeFormat;
	}

	public String getSequenceKey(LocalDateTime date) {
		String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return prefix + ":" + dateStr;
	}

	public String generateCode(LocalDateTime date, long sequence) {
		String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return String.format(codeFormat, dateStr, sequence);
	}
}