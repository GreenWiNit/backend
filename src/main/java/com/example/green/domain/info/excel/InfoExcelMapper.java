package com.example.green.domain.info.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InfoExcelMapper implements ExcelDataMapper<InfoSearchResponseByAdmin> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "info" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<InfoSearchResponseByAdmin> getDataType() {
		return InfoSearchResponseByAdmin.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("카테고리", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("정보공유코드", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("정보공유명", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("등록자", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("전시", singleBackGroundColor, FieldFormat.TEXT)
		);
	}

	@Override
	public Object[] extractRowData(InfoSearchResponseByAdmin data) {
		return new Object[] {
			data.infoCategoryName(),
			data.id(),
			data.title(),
			data.createdBy(),
			data.isDisplay()
		};
	}

	@Override
	public String getSheetName() {
		return ExcelDataMapper.super.getSheetName();
	}

}
