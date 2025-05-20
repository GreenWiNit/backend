package com.example.green.unit.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.utils.UriValidator;

class UriValidatorTest {

	private final UriValidator validator = new UriValidator();

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"      "})
	void null_empty_blank가_주어지면_false를_반환한다(String uriString) {
		// when
		boolean result = validator.isValidUri(uriString);

		// then
		assertThat(result).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {"http://", "http://example.com/illegal\\path"})
	void URISyntaxException을_발생시키는_URI는_false를_반환한다(String invalidUri) {
		/*
		 * given
		 * 1. 분석 할 수 없는 경우
		 * 2. 스킴만 있는 경우
		 * */
		// when
		boolean result = validator.isValidUri(invalidUri);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void 상대_URI는_false를_반환한다() {
		// given
		String relativeUri = "path/to/resource";

		// when
		boolean result = validator.isValidUri(relativeUri);

		// then
		assertThat(result).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"https://example.com",                                // http
		"http://example.com/path",                            // path variable
		"http://example.com/path?query=value",                // query param
		"http://example.com/path?query=value#fragment",        // fragment
		"http://user:password@example.com",                    // address
		"ftp://ftp.example.com",                            // other scheme
	})
	void 유효한_절대_URI는_true를_반환한다(String uriString) {
		// when
		boolean result = validator.isValidUri(uriString);

		// then
		assertThat(result).isTrue();
	}
}