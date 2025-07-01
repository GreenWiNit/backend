package com.example.green.global.api.page;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class PaginationTest {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;
	private static final int MAX_SIZE = 100;

	long totalElements = 100L;
	Integer currentPage = 1;
	Integer pageSize = 10;

	@Test
	void 페이지는_총_데이터수_현재_페이지_수_페이지_크기로_구성된다() {
		// given
		// when
		Pagination pagination = Pagination.of(totalElements, currentPage, pageSize);

		// then
		assertThat(pagination.getTotalElements()).isEqualTo(totalElements);
		assertThat(pagination.getCurrentPage()).isEqualTo(currentPage);
		assertThat(pagination.getPageSize()).isEqualTo(pageSize);
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	@NullSource
	void 현재_페이지_수가_없어도_기본_값이_설정된다(Integer invalidCurrentPage) {
		// given
		// when
		Pagination pagination = Pagination.of(totalElements, invalidCurrentPage, pageSize);

		// then
		assertThat(pagination.getCurrentPage()).isEqualTo(DEFAULT_PAGE);
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	@NullSource
	void 페이지_사이즈가_없어도_기본_페이지_사이즈_값이_설정된다(Integer invalidPageSize) {
		// given
		// when
		Pagination pagination = Pagination.of(totalElements, currentPage, invalidPageSize);

		// then
		assertThat(pagination.getPageSize()).isEqualTo(DEFAULT_SIZE);
	}

	@Test
	void 페이지_사이즈가_너무커도_맥스_값이_정해져_있다() {
		// given
		// when
		Pagination pagination = Pagination.of(totalElements, currentPage, MAX_SIZE + 1);

		// then
		assertThat(pagination.getPageSize()).isEqualTo(MAX_SIZE);
	}

	@Test
	void 검색_조건으로_페이지_네이션을_만들_수_있다() {
		// given
		TestPageCondition testPageCondition = new TestPageCondition(currentPage, pageSize);

		// when
		Pagination pagination = Pagination.fromCondition(testPageCondition, totalElements);

		// then
		assertThat(pagination.getTotalElements()).isEqualTo(totalElements);
		assertThat(pagination.getCurrentPage()).isEqualTo(currentPage);
		assertThat(pagination.getPageSize()).isEqualTo(pageSize);
	}

	@Test
	void 총_페이지_수도_알_수_있다() {
		// given
		Pagination pagination = Pagination.of(totalElements, currentPage, pageSize);

		// when
		int totalPages = pagination.getTotalPages();

		// then
		assertThat(totalPages).isEqualTo(10);
	}

	@Test
	void 페이지_오프셋_정보도_알_수_있다() {
		// given
		Pagination pagination = Pagination.of(totalElements, 2, pageSize);

		// when
		long offset = pagination.calculateOffset();

		// then
		assertThat(offset).isEqualTo(10);
	}

	@Test
	void 다음_페이지가_있고_이전_페이지가_없는지_알_수_있다() {
		// given
		Pagination pagination = Pagination.of(totalElements, currentPage, pageSize);

		// when
		boolean next = pagination.hasNext();
		boolean previous = pagination.hasPrevious();

		// then
		assertThat(next).isTrue();
		assertThat(previous).isFalse();
	}

	@Test
	void 이전_페이지가_있고_다음_페이지가_없는지_알_수_있다() {
		// given
		Pagination pagination = Pagination.of(totalElements, 10, pageSize);

		// when
		boolean next = pagination.hasNext();
		boolean previous = pagination.hasPrevious();

		// then
		assertThat(next).isFalse();
		assertThat(previous).isTrue();
	}
}