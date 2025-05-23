package com.example.green.domain.file.utils;

import org.springframework.stereotype.Component;

import com.example.green.global.utils.IdUtils;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileKeyGenerator {

	private static final String FILE_KEY_FORMAT = "%s/%s/%s/%s_%s%s";
	private static final String FILE_DATE_FORMAT = "yyyyMMdd";
	private static final int FILE_KEY_SUFFIX_LENGTH = 8;

	private final IdUtils idUtils;
	private final TimeUtils timeUtils;

	public String generate(String purpose, String originalFilename) {
		return String.format(FILE_KEY_FORMAT,
			purpose,
			idUtils.generateUniqueId(FILE_KEY_SUFFIX_LENGTH).substring(0, 2),
			timeUtils.getFormattedDate(FILE_DATE_FORMAT),
			idUtils.generateUniqueId(FILE_KEY_SUFFIX_LENGTH),
			timeUtils.getCurrentTimeMillis(),
			extractExtension(originalFilename));
	}

	private String extractExtension(String originalFileName) {
		String lowerCaseFileName = originalFileName.toLowerCase();
		int index = lowerCaseFileName.lastIndexOf(".");
		if (index < 0) {
			return "";
		}
		return lowerCaseFileName.substring(index);
	}
}
