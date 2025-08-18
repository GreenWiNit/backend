package com.example.integration.common;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.core.ExcelDownloader;

public abstract class ExcelDownloadTestBase<T> extends BaseIntegrationTest {

	@Autowired
	protected ExcelDownloader excelDownloader;

	// 각 도메인별 테스트에서 구현
	protected abstract ExcelDataMapper<T> getMapper();

	protected abstract List<T> createTestData();

	/*
	 * 다른건 TestExcelDataMapper 로 체크 완료
	 * 엑셀 파일 별 필드명과 데이터에 따른 Row 수만 테스트
	 * */
	@Test
	void 각_매퍼별로_엑셀이_정상적으로_다운로드_된다() throws IOException {
		// given
		List<T> testData = createTestData();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		// when
		excelDownloader.downloadAsStream(testData, mockResponse);

		// then
		verifyExcelStructure(mockResponse.getContentAsByteArray(), testData.size());
	}

	private void verifyExcelStructure(byte[] excelBytes, int expectedDataCount) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
			Sheet sheet = workbook.getSheetAt(0);
			int expectedColCount = getMapper().getFields().size();

			// 행 검증
			verifyRow(expectedDataCount, sheet, (short)expectedColCount);
			// 마지막 행 개수 검증
			assertThat(sheet.getLastRowNum()).isEqualTo(expectedDataCount);
		}
	}

	private static void verifyRow(int expectedDataCount, Sheet sheet, short expectedColCount) {
		for (int rowIndex = 0; rowIndex <= expectedDataCount; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			assertThat(row).isNotNull();
			assertThat(row.getLastCellNum()).isEqualTo(expectedColCount);
		}
	}
}