package com.example.integration.excel;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.core.ExcelDataMapperRegistry;
import com.example.integration.common.BaseIntegrationTest;

class ExcelDataMapperRegistryIntTest extends BaseIntegrationTest {

	@Autowired
	private ExcelDataMapperRegistry excelDataMapperRegistry;
	@Autowired
	private TestExcelDataMapper testExcelDataMapper;

	@Test
	void Spring_DI에_의해_자동으로_Mapper가_등록된다() {
		// given
		Class<TestDto> dataType = testExcelDataMapper.getDataType();

		// when
		ExcelDataMapper<TestDto> mapper = excelDataMapperRegistry.getMapper(dataType);

		// then
		assertThat(mapper).isEqualTo(testExcelDataMapper);
	}
}
