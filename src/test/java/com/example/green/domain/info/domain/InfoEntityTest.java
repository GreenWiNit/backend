package com.example.green.domain.info.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.info.domain.vo.InfoCategory;

/**
 * 정보(Info) 관련 도메인의 단위 테스트를 진행하는 테스트 클래스
 */
class InfoEntityTest {
	private InfoCategory infoCategory;

	@BeforeEach
	void setUp() {
		infoCategory = InfoCategory.PARTICIPANT;
	}

	@Test
	void 정보_도메인을_생성한다() {
		//given & when
		InfoEntity infoEntity = createInfo(infoCategory);

		//then
		assertThat(infoEntity.getTitle()).isEqualTo("title");
		assertThat(infoEntity.getInfoCategory().getDescription()).isEqualTo("참여형");
	}

	@Test
	void 정보_도메인을_수정한다() {
		//given
		InfoEntity infoEntity = createInfo(infoCategory);

		//when
		InfoCategory updateInfoCategory = mock(InfoCategory.class);
		when(updateInfoCategory.getDescription()).thenReturn("커뮤니티");
		when(updateInfoCategory.getCode()).thenReturn("COMMUNITIY");

		infoEntity.update(
			"updateTitle",
			"updateContent",
			updateInfoCategory,
			"updateImageUrl",
			"N",
			"updateRegisterId"
		);

		// then
		assertThat(infoEntity.getTitle()).isEqualTo("updateTitle");
		assertThat(infoEntity.getContent()).isEqualTo("updateContent");
		assertThat(infoEntity.getInfoCategory().getDescription()).isEqualTo("커뮤니티");
	}

	// 테스트 편의용
	private static InfoEntity createInfo(InfoCategory infoCategory) {
		return InfoEntity.builder()
			.title("title")
			.content("content")
			.infoCategory(infoCategory)
			.imageUrl("imageUrl")
			.isDisplay("Y")
			.registerId("admin")
			.build();
	}
}