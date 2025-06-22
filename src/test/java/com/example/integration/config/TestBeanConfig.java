package com.example.integration.config;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;

import com.example.integration.excel.ExcelDownloaderIntTest;
import com.example.integration.excel.TestExcelDataMapper;

@TestComponent
public class TestBeanConfig {

	@Bean
	public TestExcelDataMapper testExcelDataMapper() {
		return new TestExcelDataMapper();
	}

	@Bean
	public ExcelDownloaderIntTest.ExcelTestController excelTestController() {
		return new ExcelDownloaderIntTest.ExcelTestController();
	}
}
