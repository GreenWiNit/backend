package com.example.green.domain.pointshop.item.excel;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.style.ExcelField;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointItemExcelMapper implements ExcelDataMapper<PointItemSearchResponse> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "point-item" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<PointItemSearchResponse> getDataType() {
		return PointItemSearchResponse.class;
	}

	public List<ExcelField> getFields() {
		return Arrays.stream(PointItemExcelStyle.values())
			.map(style -> ExcelField.of(style.getHeader(), style.getBgColor(), style.getFormat()))
			.toList();
	}

	@Override
	public Object[] extractRowData(PointItemSearchResponse data) {
		return new Object[] {
			data.getCode(),
			data.getName(),
			data.getPointPrice(),
			data.getDisplayStatus().getValue(),
			data.getCreatedDate()
		};
	}

}
