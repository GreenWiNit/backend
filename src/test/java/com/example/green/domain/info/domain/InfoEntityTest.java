package com.example.green.domain.info.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.sql.spi.NativeQueryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.domain.info.utils.PrefixSequenceIdGenerator;

/**
 * 정보(Info) 관련 도메인의 단위 테스트를 진행하는 테스트 클래스
 */

@ExtendWith(MockitoExtension.class)
class InfoEntityTest {
	@Mock
	SharedSessionContractImplementor session;
	@Mock
	NativeQueryImplementor<String> query;
	PrefixSequenceIdGenerator generator = new PrefixSequenceIdGenerator();
	private InfoCategory infoCategory;

	// 테스트 편의용
	private static InfoEntity createInfo(InfoCategory infoCategory) {
		return InfoEntity.builder()
			.title("title")
			.content("content")
			.infoCategory(infoCategory)
			.imageUrl("imageUrl")
			.isDisplay("Y")
			.build();
	}

	@BeforeEach
	void setUp() {
		infoCategory = InfoCategory.PARTICIPANT;
	}

	@Test
	void 정보커스텀ID_첫_생성_케이스_테스트() {
		when(session.createNativeQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(null);

		Serializable id = generator.generate(session, new Object());
		assertEquals("P000001", id);
	}

	@Test
	void 정보커스텀ID_기존_값_있을_때_증가_테스트() {
		when(session.createNativeQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn("P000042");

		Serializable id = generator.generate(session, new Object());
		assertEquals("P000043", id);
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
			"N"
		);

		// then
		assertThat(infoEntity.getTitle()).isEqualTo("updateTitle");
		assertThat(infoEntity.getContent()).isEqualTo("updateContent");
		assertThat(infoEntity.getInfoCategory().getDescription()).isEqualTo("커뮤니티");
	}
}