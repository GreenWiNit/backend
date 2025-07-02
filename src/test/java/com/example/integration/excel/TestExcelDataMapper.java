package com.example.integration.excel;

import java.util.List;

import org.springframework.boot.test.context.TestComponent;

import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;

@TestComponent
public class TestExcelDataMapper implements ExcelDataMapper<TestDto> {

	@Override
	public String getFileName() {
		return "test";
	}

	@Override
	public Class<TestDto> getDataType() {
		return TestDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("ID", singleBackGroundColor, FieldFormat.of("#@")),
			ExcelField.of("이름", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("나이", singleBackGroundColor, FieldFormat.NUMBER),
			ExcelField.of("생년월일", singleBackGroundColor, FieldFormat.DATE),
			ExcelField.of("적립 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("가입 일자", singleBackGroundColor, FieldFormat.DATETIME)
		);
	}

	@Override
	public Object[] extractRowData(TestDto data) {
		return new Object[] {
			data.getId(),
			data.getName(),
			data.getAge(),
			data.getBirthDate(),
			data.getPoint(),
			data.getJoinedAt()
		};
	}
}