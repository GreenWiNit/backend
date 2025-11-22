package com.example.green.domain.pointshop.item.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.ExcelField;
import com.example.green.infra.excel.style.FieldFormat;

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
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("아이템 코드", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("아이템명", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("아이템 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("수량", singleBackGroundColor, FieldFormat.NUMBER),
			ExcelField.of("판매 상태", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("전시 여부", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("등록일", singleBackGroundColor, FieldFormat.DATE)
		);
	}

	@Override
	public Object[] extractRowData(PointItemSearchResponse data) {
		return new Object[] {
			data.getCode(),
			data.getName(),
			data.getPointPrice(),
			data.getStockQuantity(),
			data.getSellingStatus().getValue(),
			data.getDisplayStatus().getValue(),
			data.getCreatedDate()
		};
	}

}
