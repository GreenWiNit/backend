package com.example.green.global.excel.core;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;
import com.example.green.global.excel.style.ExcelField;

class ExcelDataMapperRegistryTest {

	@Test
	void 정상적으로_매퍼를_등록하고_조회할_수_있다() {
		// given
		MockUserDataMapper mockUserDataMapper = new MockUserDataMapper();
		List<ExcelDataMapper<?>> mappers = Arrays.asList(mockUserDataMapper);
		ExcelDataMapperRegistry registry = new ExcelDataMapperRegistry(mappers);
		MockUser mockUser = new MockUser("test");

		// when
		ExcelDataMapper<MockUser> mapper = registry.getMapper(MockUser.class);

		// then
		String sheetName = mapper.getSheetName();
		List<ExcelField> fields = mapper.getFields();
		Object[] objects = mapper.extractRowData(mockUser);
		assertThat(sheetName).isEqualTo(mockUserDataMapper.getSheetName());
		assertThat(fields).isEqualTo(mockUserDataMapper.getFields());
		assertThat(objects).isEqualTo(mockUserDataMapper.extractRowData(mockUser));
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

	class MockUser {
		private String name;

		public MockUser(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	class MockUserDataMapper implements ExcelDataMapper<MockUser> {

		@Override
		public String getFileName() {
			return "";
		}

		@Override
		public Class<MockUser> getDataType() {
			return MockUser.class;
		}

		@Override
		public List<ExcelField> getFields() {
			return List.of();
		}

		@Override
		public Object[] extractRowData(MockUser data) {
			return new Object[] {
				data.getName()
			};
		}
	}

	public class AnotherMockUserDataMapper implements ExcelDataMapper<MockUser> {
		@Override
		public String getFileName() {
			return "Test";
		}

		@Override
		public Class<MockUser> getDataType() {
			return MockUser.class;
		}

		@Override
		public List<ExcelField> getFields() {
			return List.of();
		}

		@Override
		public Object[] extractRowData(MockUser data) {
			return new Object[0];
		}
	}

	class UnregisteredType {
	}
}