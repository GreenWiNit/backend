package com.example.integration.excel;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.green.infra.excel.exception.ExcelExceptionMessage;
import com.example.integration.common.ExcelDownloadTestBase;

public class ExcelDownloaderIntTest extends ExcelDownloadTestBase<TestDto> {

	@Autowired
	private TestExcelDataMapper testExcelDataMapper;

	@Override
	protected TestExcelDataMapper getMapper() {
		return testExcelDataMapper;
	}

	@Override
	protected List<TestDto> createTestData() {
		List<TestDto> dto = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			dto.add(new TestDto());
		}
		return dto;
	}

	@Test
	void 엑셀_파일_다운로드_테스트() {
		// given
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		String fileName = testExcelDataMapper.getFileName();

		// when
		excelDownloader.downloadAsStream(createTestData(), mockResponse);

		// then
		assertThat(mockResponse.getContentType())
			.isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		assertThat(mockResponse.getHeader("Content-Disposition"))
			.isEqualTo("attachment; filename=\"" + fileName + ".xlsx\"");
	}

	@Test
	void 빈_데이터로_엑셀_다운로드시_예외_발생() {
		// given
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		// when & then
		assertThatThrownBy(() -> excelDownloader.downloadAsStream(List.of(), mockResponse))
			.isInstanceOf(Exception.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ExcelExceptionMessage.EMPTY_DATA);
	}
}
