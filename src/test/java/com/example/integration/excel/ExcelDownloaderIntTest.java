package com.example.integration.excel;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.global.excel.core.ExcelDownloader;
import com.example.integration.common.BaseIntegrationTest;

import jakarta.servlet.http.HttpServletResponse;

public class ExcelDownloaderIntTest extends BaseIntegrationTest {

	private static final int DATA_ROW_SIZE = 10;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@RestController
	public static class ExcelTestController {

		@Autowired
		private ExcelDownloader excelDownloader;

		@GetMapping("/excel/down")
		public void download(HttpServletResponse response) {
			List<TestDto> dto = new ArrayList<>();
			for (int i = 0; i < DATA_ROW_SIZE; i++) {
				dto.add(new TestDto());
			}
			excelDownloader.downloadAsStream(dto, response);
		}
	}

	@Test
	void 엑셀_파일_다운로드_테스트() throws IOException {
		// when
		String url = "http://localhost:" + port + "/excel/down";
		ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

		// then
		verifyHttpResponse(response);
		verifyExcelFile(response.getBody());
	}

	private static void verifyHttpResponse(ResponseEntity<byte[]> response) {
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType().toString())
			.contains("spreadsheetml");
		assertThat(response.getHeaders().get("Content-Disposition"))
			.anyMatch(header -> header.contains("attachment"));
	}

	private void verifyExcelFile(byte[] excelBytes) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
			assertThat(workbook.getNumberOfSheets()).isEqualTo(1);

			Sheet sheet = workbook.getSheetAt(0);
			assertThat(sheet.getLastRowNum()).isEqualTo(DATA_ROW_SIZE);
		}
	}
}
