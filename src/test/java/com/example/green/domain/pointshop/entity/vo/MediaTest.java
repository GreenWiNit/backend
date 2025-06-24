package com.example.green.domain.pointshop.entity.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class MediaTest {

	@Test
	void 미디어_정보를_생성한다() {
		// given
		String thumbnailUrl = "https://example.com/image.jpg";
		// when
		Media media = new Media(thumbnailUrl);
		// then
		assertThat(media.thumbnailUrl()).isEqualTo(thumbnailUrl);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = "NO_URI")
	void 미디어_정보의_썸네일_이미지는_필수_정보로_URI_형식이_아니라면_생성할_수_없다(String invalidThumbnailUrl) {
		// given
		// when & then
		assertThatThrownBy(() -> new Media(invalidThumbnailUrl))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_THUMBNAIL);
	}
}