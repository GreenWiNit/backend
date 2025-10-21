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
			.imageUrl("https://example.com/test.jpg")
			.isDisplay("Y")
			.build();
	}

	@Test
	void 정보_이미지를_생성한다() {
		// given
		InfoEntity info = createTestInfo();
		String imageUrl = "https://example.com/image1.jpg";
		Integer displayOrder = 0;

		// when
		InfoImage infoImage = InfoImage.create(info, imageUrl, displayOrder);

		// then
		assertThat(infoImage.getInfo()).isEqualTo(info);
		assertThat(infoImage.getImageUrl()).isEqualTo(imageUrl);
		assertThat(infoImage.getDisplayOrder()).isEqualTo(0);
	}

	@Test
	void 정보가_null이면_예외를_던진다() {
		// given
		InfoEntity nullInfo = null;
		String imageUrl = "https://example.com/image1.jpg";
		Integer displayOrder = 0;

		// when & then
		assertThatThrownBy(() -> InfoImage.create(nullInfo, imageUrl, displayOrder))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 이미지URL이_비어있으면_예외를_던진다() {
		// given
		InfoEntity info = createTestInfo();
		String emptyUrl = "";
		Integer displayOrder = 0;

		// when & then
		assertThatThrownBy(() -> InfoImage.create(info, emptyUrl, displayOrder))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void displayOrder가_음수면_예외를_던진다() {
		// given
		InfoEntity info = createTestInfo();
		String imageUrl = "https://example.com/image1.jpg";
		Integer negativeOrder = -1;

		// when & then
		assertThatThrownBy(() -> InfoImage.create(info, imageUrl, negativeOrder))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("이미지 순서는 0 이상이어야 합니다.");
	}

	@Test
	void displayOrder가_0이면_정상적으로_생성된다() {
		// given
		InfoEntity info = createTestInfo();
		String imageUrl = "https://example.com/image1.jpg";
		Integer displayOrder = 0;

		// when
		InfoImage infoImage = InfoImage.create(info, imageUrl, displayOrder);

		// then
		assertThat(infoImage.getDisplayOrder()).isEqualTo(0);
	}
}
