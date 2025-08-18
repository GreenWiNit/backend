package com.example.green.domain.pointshop.product.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.ExcelField;
import com.example.green.infra.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointProductExcelMapper implements ExcelDataMapper<PointProductSearchResult> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "point-product" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<PointProductSearchResult> getDataType() {
		return PointProductSearchResult.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("상품 코드", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("상품명", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("교환 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("수량", singleBackGroundColor, FieldFormat.NUMBER),
			ExcelField.of("판매 상태", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("전시 여부", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("등록일", singleBackGroundColor, FieldFormat.DATE)
		);
	}

	@Override
	public Object[] extractRowData(PointProductSearchResult data) {
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
