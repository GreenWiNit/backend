package com.example.green.global.api.page;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class PageTemplateTest {

	@Test
	void 페이지네이션_정보로_페이지_템플릿을_구성할_수_있다() {
		// given
		Pagination pagination = Pagination.of(100, 1, 10);

		// when
		PageTemplate<Object> result = PageTemplate.of(List.of(), pagination);

		// then
		assertThat(result.content()).isEqualTo(List.of());
		assertThat(result.pageSize()).isEqualTo(10);
		assertThat(result.currentPage()).isEqualTo(1);
		assertThat(result.totalPages()).isEqualTo(10);
		assertThat(result.totalElements()).isEqualTo(100);
		assertThat(result.hasNext()).isTrue();
	}

	@Test
	void 페이지네이션_정보로_리스트_정보가_비어도_추가된다() {
		// given
		Pagination pagination = Pagination.of(100, 1, 10);

		// when
		PageTemplate<Object> result = PageTemplate.of(null, pagination);

		// then
		assertThat(result.content()).isEqualTo(List.of());
	}
}