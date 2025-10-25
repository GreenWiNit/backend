package com.example.green.domain.challenge.entity.challenge.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class ChallengeContentTest {

	@Test
	void 콘텐츠내용과_이미지_정보로_생성한다() {
		// given
		String content = "newContent";
		String imageUrl = "https://image.url/test.img";

		// when
		ChallengeContent result = ChallengeContent.of(content, imageUrl);

		// then
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getImageUrl()).isEqualTo(imageUrl);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 콘텐츠_내용은_비어있을_수_없다(String emptyContent) {
		// given
		String imageUrl = "https://image.url/test.img";

		// when & then
		assertThatThrownBy(() -> ChallengeContent.of(emptyContent, imageUrl))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_CONTENT_BLANK);
	}

	@Test
	void 이미지는_유효한_URI_형식이어야_한다() {
		// given
		String content = "newContent";
		String invalidImageUrl = "https/image.url/test.img";

		// when & then
		assertThatThrownBy(() -> ChallengeContent.of(content, invalidImageUrl))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_CHALLENGE_IMAGE);
	}
}