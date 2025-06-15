package com.example.green.global.excel.core;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;

class ExcelDataMapperRegistryTest {

	@Test
	void 정상적으로_Exporter를_등록하고_조회할_수_있다() {
		// given
		List<ExcelDataMapper<?>> mappers = Arrays.asList(new MockUserDataMapper());

		// when
		ExcelDataMapperRegistry registry = new ExcelDataMapperRegistry(mappers);

		// then
		ExcelDataMapper<MockUser> result = registry.getMapper(MockUser.class);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(MockUserDataMapper.class);
	}

	@Test
	void 등록되지_않은_타입_조회_시_예외가_발생한다() {
		// given
		List<ExcelDataMapper<?>> mappers = Arrays.asList(new MockUserDataMapper());
		ExcelDataMapperRegistry registry = new ExcelDataMapperRegistry(mappers);

		// when & then
		assertThatThrownBy(() -> registry.getMapper(UnregisteredType.class))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
	}

	@Test
	void 중복된_타입_등록_시_경고를_출력한다() {
		// given - 같은 타입을 처리하는 두 개의 Mapper
		List<ExcelDataMapper<?>> mappers = Arrays.asList(
			new MockUserDataMapper(),
			new AnotherMockUserDataMapper()
		);

		// when & then
		assertThatThrownBy(() -> new ExcelDataMapperRegistry(mappers))
			.isInstanceOf(IllegalStateException.class);
	}
}