package com.example.green.global.api.page;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class CursorTemplateTest {

	static class DummyDto {
	}

	@Test
	void 데이터만_담는다() {
		// given
		List<DummyDto> data = List.of(new DummyDto());

		// when
		CursorTemplate<Long, DummyDto> result = CursorTemplate.of(data);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).isEqualTo(data);
	}

	@Test
	void 데이터가_없으면_빈_리스트를_반환한다() {
		// when
		CursorTemplate<Long, DummyDto> result = CursorTemplate.of(List.of());

		// then
		assertThat(result.content()).hasSize(0);
	}

	@Test
	void 커서_템플릿을_생성한다() {
		// given

		// when
		CursorTemplate<Long, Object> result = CursorTemplate.ofWithNextCursor(2L, List.of());

		// then
		assertThat(result.hasNext()).isTrue();
		assertThat(result.nextCursor()).isEqualTo(2L);
		assertThat(result.content()).hasSize(0);
	}

	@Test
	void 복합_커서를_사용할_수_있다() {
		// given
		String composite = "compositeCursor12421";

		// when
		CursorTemplate<String, Object> result = CursorTemplate.ofWithNextCursor(composite, List.of());

		// then
		assertThat(result.nextCursor()).isEqualTo(composite);
	}
}