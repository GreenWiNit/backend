package com.example.green.domain.info.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.global.error.exception.BusinessException;

class InfoImageTest {

	private static InfoEntity createTestInfo() {
		return InfoEntity.builder()
			.title("테스트 제목")
			.content("테스트 내용")
			.infoCategory(InfoCategory.CONTENTS)
			.imageUrls(java.util.Arrays.asList("https://example.com/test.jpg"))
			.isDisplay("Y")
			.build();
	}

	@Test
	void 정보_이미지를_생성한다() {
		// given
		InfoEntity info = createTestInfo();
		String imageUrl = "https://example.com/image1.jpg";

		// when
		InfoImage infoImage = InfoImage.create(info, imageUrl);

		// then
		assertThat(infoImage.getInfo()).isEqualTo(info);
		assertThat(infoImage.getImageUrl()).isEqualTo(imageUrl);
	}

	@Test
	void 정보가_null이면_예외를_던진다() {
		// given
		InfoEntity nullInfo = null;
		String imageUrl = "https://example.com/image1.jpg";

		// when & then
		assertThatThrownBy(() -> InfoImage.create(nullInfo, imageUrl))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 이미지URL이_비어있으면_예외를_던진다() {
		// given
		InfoEntity info = createTestInfo();
		String emptyUrl = "";

		// when & then
		assertThatThrownBy(() -> InfoImage.create(info, emptyUrl))
			.isInstanceOf(BusinessException.class);
	}
}
