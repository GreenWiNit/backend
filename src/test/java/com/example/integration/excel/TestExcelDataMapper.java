package com.example.integration.excel;

import java.util.List;

import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.DataFormat;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FormatType;

public class TestExcelDataMapper implements ExcelDataMapper<TestDto> {

	@Override
	public Class<TestDto> getDataType() {
		return TestDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("ID", singleBackGroundColor, DataFormat.of("#@", FormatType.TEXT)),
			ExcelField.of("이름", singleBackGroundColor, DataFormat.TEXT),
			ExcelField.of("나이", singleBackGroundColor, DataFormat.NUMBER),
			ExcelField.of("생년월일", singleBackGroundColor, DataFormat.DATE),
			ExcelField.of("적립 포인트", singleBackGroundColor, DataFormat.POINT),
			ExcelField.of("가입 일자", singleBackGroundColor, DataFormat.DATETIME)
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