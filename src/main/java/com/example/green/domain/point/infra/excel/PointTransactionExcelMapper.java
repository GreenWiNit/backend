package com.example.green.domain.point.infra.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.ExcelField;
import com.example.green.infra.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionExcelMapper implements ExcelDataMapper<PointTransactionDto> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "point_transaction_" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<PointTransactionDto> getDataType() {
		return PointTransactionDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("날짜", singleBackGroundColor, FieldFormat.DATE),
			ExcelField.of("구분", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("내용", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("적립 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("차감 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("누적 포인트", singleBackGroundColor, FieldFormat.POINT)
		);
	}

	@Override
	public Object[] extractRowData(PointTransactionDto data) {
		return new Object[] {
			data.transactionAt(),
			data.type().getValue(),
			data.description(),
			data.earnedAmount(),
			data.spentAmount(),
			data.balanceAfter()
		};
	}
}
