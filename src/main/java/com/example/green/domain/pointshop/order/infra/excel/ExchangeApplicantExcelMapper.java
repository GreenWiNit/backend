package com.example.green.domain.pointshop.order.infra.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationResult;
import com.example.green.infra.excel.core.ExcelDataMapper;
import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.ExcelField;
import com.example.green.infra.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExchangeApplicantExcelMapper implements ExcelDataMapper<ExchangeApplicationResult> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "point-product-exchange-applicant" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<ExchangeApplicationResult> getDataType() {
		return ExchangeApplicationResult.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor singleBackGroundColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("신청일자", singleBackGroundColor, FieldFormat.DATE),
			ExcelField.of("MemberKey", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("사용자 이메일", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("상품코드", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("수량", singleBackGroundColor, FieldFormat.NUMBER),
			ExcelField.of("차감 포인트", singleBackGroundColor, FieldFormat.POINT),
			ExcelField.of("이름", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("전화번호", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("주소", singleBackGroundColor, FieldFormat.TEXT),
			ExcelField.of("상품 처리 상태", singleBackGroundColor, FieldFormat.TEXT)
		);
	}

	@Override
	public Object[] extractRowData(ExchangeApplicationResult data) {
		return new Object[] {
			data.getExchangedAt(),
			data.getMemberKey(),
			data.getMemberEmail(),
			data.getPointProductCode(),
			data.getQuantity(),
			data.getTotalPrice(),
			data.getRecipientName(),
			data.getRecipientPhoneNumber(),
			data.getFullAddress(),
			data.getStatus().getValue()
		};
	}
}
