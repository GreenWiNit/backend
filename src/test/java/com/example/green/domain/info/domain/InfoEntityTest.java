package com.example.green.domain.info.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.sql.spi.NativeQueryImplementor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.exception.InfoException;
import com.example.green.domain.info.utils.PrefixSequenceIdGenerator;
import com.example.green.global.error.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * 정보(Info) 관련 도메인의 단위 테스트를 진행하는 테스트 클래스
 * - PrefixSequenceIdGenerator 클래스도 Entity 생성 시 사용되므로 함께 테스트
 */

@ExtendWith(MockitoExtension.class)
class InfoEntityTest {
	@Mock
	SharedSessionContractImplementor session;
	@Mock
	NativeQueryImplementor<String> query;

	// 테스트 편의용
	private static InfoEntity createInfo(String isDisplay) {
		return InfoEntity.builder()
			.title("title")
			.content("content")
			.infoCategory(InfoCategory.CONTENTS)
			.imageUrl("imageUrl")
			.isDisplay(isDisplay)
			.build();
	}

	@Test
	void 정보커스텀ID_첫_생성_케이스_테스트() {
		when(session.createNativeQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(null);

		PrefixSequenceIdGenerator generator = new PrefixSequenceIdGenerator();

		Serializable id = generator.generate(session, new Object());
		assertEquals("P000001", id);
	}

	@Test
	void 정보커스텀ID_기존_값_있을_때_증가_테스트() {
		when(session.createNativeQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn("P000042");

		PrefixSequenceIdGenerator generator = new PrefixSequenceIdGenerator();

		Serializable id = generator.generate(session, new Object());
		assertEquals("P000043", id);
	}

	@Test
	void 정보커스텀ID_생성시_오류가_발생하면_예외를_던진다() {
		when(session.createNativeQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenThrow(new RuntimeException("DB Error"));

		PrefixSequenceIdGenerator generator = new PrefixSequenceIdGenerator();

		assertThatThrownBy(() -> generator.generate(session, new Object()))
			.isInstanceOf(InfoException.class)
			.hasMessageContaining("정보 ID 생성 중 오류가 발생했습니다.");
	}

	@ParameterizedTest
	@ValueSource(strings = {"notValid", "n"})
	void 전시여부가_YN또는yn가_아니면_예외를_던진다(String value) {
		//given
		String isDisplay = value;

		//when & then
		if (value.equals("notValid")) {
			assertThatThrownBy(() -> createInfo(isDisplay))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining("서버에서 본문을 처리할 수 없습니다.");
		} else {
			assertThat(createInfo(isDisplay).getIsDisplay()).isEqualTo("N");
		}
	}

	@Test
	void 정보_도메인을_생성한다() {
		//given & when
		InfoEntity infoEntity = createInfo("Y");

		//then
		assertThat(infoEntity.getTitle()).isEqualTo("title");
		assertThat(infoEntity.getInfoCategory().getDescription()).isEqualTo("콘텐츠");
	}

	@Test
	void 정보_도메인을_수정한다() {
		//given
		InfoEntity infoEntity = createInfo("Y");

		//when
		InfoCategory updateInfoCategory = mock(InfoCategory.class);
		when(updateInfoCategory.getDescription()).thenReturn("기타");

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
		assertThat(infoEntity.getInfoCategory().getDescription()).isEqualTo("기타");
	}

	/**
	 * ENUM 타입인 정보 카테고리(InfoCategory) 파싱 테스트
	 * */
	@ParameterizedTest
	@ValueSource(strings = {"CONTENTS", "EVENT", "ETC"})
	void 정보_카테고리ENUM에_등록된_값이_들어왔을_때_정상적으로_변환된다(String validCode) throws JsonProcessingException {
		// given
		ObjectMapper objectMapper = new ObjectMapper();
		String json = String.format("{\"infoCategory\":\"%s\"}", validCode);
		InfoRequest req = objectMapper.readValue(json, InfoRequest.class);

		// when & then
		assertThat(req.infoCategory().toString()).isEqualTo(validCode);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "NOT_EXISTING"})
	void 정보_카테고리ENUM에_등록되지_않은_값이_들어왔을_때_예외를_던진다(String invalidCode) {
		// given
		ObjectMapper objectMapper = new ObjectMapper();
		String json = String.format("{\"infoCategory\":\"%s\"}", invalidCode);

		// when & then
		// Jackson이 파싱 중에 InvalidFormatException 던짐
		assertThatThrownBy(() -> objectMapper.readValue(json, InfoRequest.class))
			.isInstanceOf(InvalidFormatException.class);
	}

	@Test
	void 정보_도메인에_다중_이미지를_추가한다() {
		// given
		InfoEntity infoEntity = createInfo("Y");
		List<String> imageUrls = Arrays.asList(
			"https://example.com/image1.jpg",
			"https://example.com/image2.jpg",
			"https://example.com/image3.jpg"
		);

		// when
		infoEntity.updateImages(imageUrls);

		// then
		assertThat(infoEntity.getImageUrls()).hasSize(3);
		assertThat(infoEntity.getImageUrls()).containsExactly(
			"https://example.com/image1.jpg",
			"https://example.com/image2.jpg",
			"https://example.com/image3.jpg"
		);
	}

	@Test
	void 정보_도메인을_다중_이미지와_함께_수정한다() {
		// given
		InfoEntity infoEntity = createInfo("Y");
		List<String> imageUrls = Arrays.asList(
			"https://example.com/new1.jpg",
			"https://example.com/new2.jpg"
		);

		// when
		infoEntity.update(
			"updateTitle",
			"updateContent",
			InfoCategory.EVENT,
			imageUrls,
			"N"
		);

		// then
		assertThat(infoEntity.getTitle()).isEqualTo("updateTitle");
		assertThat(infoEntity.getContent()).isEqualTo("updateContent");
		assertThat(infoEntity.getInfoCategory()).isEqualTo(InfoCategory.EVENT);
		assertThat(infoEntity.getImageUrls()).hasSize(2);
		assertThat(infoEntity.getImageUrls()).containsExactly(
			"https://example.com/new1.jpg",
			"https://example.com/new2.jpg"
		);
	}

	@Test
	void 이미지_목록이_비어있으면_예외를_던진다() {
		// given
		InfoEntity infoEntity = createInfo("Y");
		List<String> emptyImageUrls = Arrays.asList();

		// when & then
		assertThatThrownBy(() -> infoEntity.updateImages(emptyImageUrls))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("최소 1개 이상의 이미지가 필요합니다.");
	}

}