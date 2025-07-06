package com.example.green.global.utils;

import java.security.SecureRandom;
import java.util.Arrays;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

/**
 * ULID생성 유틸리티
 * - 시간 정보 포함 (생성 순서 정렬 가능)
 */
public class UlidUtils {

	private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
	private static final SecureRandom RANDOM = new SecureRandom();

	private UlidUtils() {
	}

	public static String generate() {
		return generate(System.currentTimeMillis());
	}

	public static String generate(long timestamp) {
		char[] chars = new char[26];

		// 시간 부분 (10자리)
		encodeTime(chars, timestamp);

		// 랜덤 부분 (16자리)
		encodeRandom(chars);

		return new String(chars);
	}

	public static long extractTimestamp(String ulid) {
		if (ulid == null || ulid.length() != 26) {
			throw new BusinessException(GlobalExceptionMessage.ULID_INVALID_FORMAT);
		}

		long timestamp = 0;
		for (int i = 0; i < 10; i++) {
			char c = ulid.charAt(i);
			int value = decodeChar(c);
			timestamp = timestamp * 32 + value;
		}

		return timestamp;
	}

	public static boolean isValid(String ulid) {
		if (ulid == null || ulid.length() != 26) {
			return false;
		}

		for (char c : ulid.toCharArray()) {
			if (Arrays.binarySearch(ENCODING, c) < 0) {
				return false;
			}
		}

		return true;
	}

	private static void encodeTime(char[] chars, long timestamp) {
		chars[0] = ENCODING[(int)(timestamp >>> 45) & 31];
		chars[1] = ENCODING[(int)(timestamp >>> 40) & 31];
		chars[2] = ENCODING[(int)(timestamp >>> 35) & 31];
		chars[3] = ENCODING[(int)(timestamp >>> 30) & 31];
		chars[4] = ENCODING[(int)(timestamp >>> 25) & 31];
		chars[5] = ENCODING[(int)(timestamp >>> 20) & 31];
		chars[6] = ENCODING[(int)(timestamp >>> 15) & 31];
		chars[7] = ENCODING[(int)(timestamp >>> 10) & 31];
		chars[8] = ENCODING[(int)(timestamp >>> 5) & 31];
		chars[9] = ENCODING[(int)timestamp & 31];
	}

	private static void encodeRandom(char[] chars) {
		byte[] randomBytes = new byte[16];
		RANDOM.nextBytes(randomBytes);

		chars[10] = ENCODING[(randomBytes[0] & 0xFF) >>> 3];
		chars[11] = ENCODING[((randomBytes[0] & 0x07) << 2) | ((randomBytes[1] & 0xC0) >>> 6)];
		chars[12] = ENCODING[(randomBytes[1] & 0x3E) >>> 1];
		chars[13] = ENCODING[((randomBytes[1] & 0x01) << 4) | ((randomBytes[2] & 0xF0) >>> 4)];
		chars[14] = ENCODING[((randomBytes[2] & 0x0F) << 1) | ((randomBytes[3] & 0x80) >>> 7)];
		chars[15] = ENCODING[(randomBytes[3] & 0x7C) >>> 2];
		chars[16] = ENCODING[((randomBytes[3] & 0x03) << 3) | ((randomBytes[4] & 0xE0) >>> 5)];
		chars[17] = ENCODING[randomBytes[4] & 0x1F];
		chars[18] = ENCODING[(randomBytes[5] & 0xFF) >>> 3];
		chars[19] = ENCODING[((randomBytes[5] & 0x07) << 2) | ((randomBytes[6] & 0xC0) >>> 6)];
		chars[20] = ENCODING[(randomBytes[6] & 0x3E) >>> 1];
		chars[21] = ENCODING[((randomBytes[6] & 0x01) << 4) | ((randomBytes[7] & 0xF0) >>> 4)];
		chars[22] = ENCODING[((randomBytes[7] & 0x0F) << 1) | ((randomBytes[8] & 0x80) >>> 7)];
		chars[23] = ENCODING[(randomBytes[8] & 0x7C) >>> 2];
		chars[24] = ENCODING[((randomBytes[8] & 0x03) << 3) | ((randomBytes[9] & 0xE0) >>> 5)];
		chars[25] = ENCODING[randomBytes[9] & 0x1F];
	}

	private static int decodeChar(char c) {
		for (int i = 0; i < ENCODING.length; i++) {
			if (ENCODING[i] == c) {
				return i;
			}
		}
		throw new BusinessException(GlobalExceptionMessage.ULID_INVALID_CHARACTER);
	}
} 