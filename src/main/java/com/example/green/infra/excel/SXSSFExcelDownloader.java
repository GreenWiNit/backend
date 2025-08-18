package com.example.green.infra.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;

import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.core.ExcelDataMapperRegistry;
import com.example.green.infra.excel.core.ExcelDownloader;
import com.example.green.infra.excel.exception.ExcelException;
import com.example.green.infra.excel.exception.ExcelExceptionMessage;
import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.ExcelField;
import com.example.green.infra.excel.style.FieldFormat;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SXSSFExcelDownloader implements ExcelDownloader { // @checkstyle:ignore
	private static final String DISPOSITION_FORMAT = "attachment; filename=\"%s.%s\"";
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	private static final int WINDOW_SIZE = 100;

	private final ExcelDataMapperRegistry registry;

	@Override
	public <T> void downloadAsStream(List<T> dataList, HttpServletResponse httpServletResponse) {
		if (dataList.isEmpty()) {
			throw new ExcelException(ExcelExceptionMessage.EMPTY_DATA);
		}

		@SuppressWarnings("unchecked") Class<T> dataType = (Class<T>)dataList.getFirst().getClass();
		ExcelDataMapper<T> mapper = registry.getMapper(dataType);
		setupExcelResponse(httpServletResponse, mapper.getFileName());

		try {
			downloadAsStream(dataList, mapper, httpServletResponse.getOutputStream());
		} catch (IOException e) {
			log.error("엑셀 파일 생성 중 오류 발생: 데이터 타입={}", mapper.getDataType().getSimpleName(), e);
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
	}

	private void setupExcelResponse(HttpServletResponse response, String fileName) {
		String disposition = String.format(DISPOSITION_FORMAT, fileName, EXCEL_FILE_EXTENSION);
		response.setHeader("Content-Disposition", disposition);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	private <T> void downloadAsStream(
		List<T> dataList,
		ExcelDataMapper<T> mapper,
		OutputStream outputStream
	) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook(WINDOW_SIZE)) {
			workbook.setCompressTempFiles(true);

			SXSSFSheet sheet = workbook.createSheet(mapper.getSheetName());
			List<ExcelField> fields = mapper.getFields();
			createHeaderRow(workbook, sheet, fields);

			List<CellStyle> cellStyles = createColumnStyles(fields, workbook);
			createDataRows(sheet, fields, dataList, mapper, cellStyles);

			workbook.write(outputStream);
			outputStream.flush();
		}
	}

	private List<CellStyle> createColumnStyles(List<ExcelField> fields, Workbook workbook) {
		return fields.stream()
			.map(field -> renderDataStyle(workbook, field.getFormat()))
			.toList();
	}

	private void createHeaderRow(Workbook workbook, Sheet sheet, List<ExcelField> fields) {
		Row headerRow = sheet.createRow(0);
		Font font = workbook.createFont();
		font.setBold(true);

		for (int i = 0; i < fields.size(); i++) {
			ExcelField field = fields.get(i);
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(field.getName());

			BackGroundColor backGroundColor = field.getBackGroundColor();
			Color color = new XSSFColor(backGroundColor.toRgb());
			CellStyle headerStyle = renderHeaderStyle(workbook, color, font);
			cell.setCellStyle(headerStyle);
		}
	}

	private <T> void createDataRows(
		Sheet sheet,
		List<ExcelField> fields,
		List<T> dataList,
		ExcelDataMapper<T> mapper,
		List<CellStyle> columnStyles
	) {
		for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
			Row dataRow = sheet.createRow(rowIndex + 1);
			T data = dataList.get(rowIndex);
			Object[] rowData = mapper.extractRowData(data);
			createDataColsByRow(fields, rowData, dataRow, columnStyles);
		}
	}

	private void createDataColsByRow(
		List<ExcelField> fields,
		Object[] rowData,
		Row dataRow,
		List<CellStyle> columnStyles
	) {
		int colCount = Math.min(fields.size(), rowData.length);
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			Cell cell = dataRow.createCell(colIndex);
			setCellValue(cell, rowData[colIndex]);
			cell.setCellStyle(columnStyles.get(colIndex));
		}
	}

	private CellStyle renderHeaderStyle(Workbook workbook, Color color, Font font) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);

		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);

		style.setFont(font);
		return style;
	}

	private CellStyle renderDataStyle(Workbook workbook, FieldFormat fieldFormat) {
		CellStyle style = workbook.createCellStyle();
		DataFormat dataFormat = workbook.createDataFormat();
		String pattern = fieldFormat.getPattern();
		short format = dataFormat.getFormat(pattern);

		style.setDataFormat(format);
		return style;
	}

	private void setCellValue(Cell cell, Object value) {
		if (value instanceof Number) {
			cell.setCellValue(Double.parseDouble(value.toString()));
			return;
		}
		if (value instanceof Temporal) {
			if (value instanceof LocalDateTime) {
				cell.setCellValue((LocalDateTime)value);
				return;
			}
			if (value instanceof LocalDate) {
				cell.setCellValue((LocalDate)value);
				return;
			}
			return;
		}
		cell.setCellValue(value.toString());
	}
}
