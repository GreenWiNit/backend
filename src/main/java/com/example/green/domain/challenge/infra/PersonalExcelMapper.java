package com.example.green.domain.challenge.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonalExcelMapper implements ExcelDataMapper<AdminPersonalChallengesDto> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "personal_challenge_" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<AdminPersonalChallengesDto> getDataType() {
		return AdminPersonalChallengesDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor headerColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("챌린지 코드", headerColor, FieldFormat.TEXT),
			ExcelField.of("챌린지 제목", headerColor, FieldFormat.TEXT),
			ExcelField.of("진행 기간", headerColor, FieldFormat.TEXT),
			ExcelField.of("포인트", headerColor, FieldFormat.POINT),
			ExcelField.of("전시여부", headerColor, FieldFormat.TEXT),
			ExcelField.of("생성일", headerColor, FieldFormat.DATE)
		);
	}

	@Override
	public Object[] extractRowData(AdminPersonalChallengesDto data) {
		return new Object[] {
			data.challengeCode(),
			data.challengeName(),
			data.getPeriod(),
			data.challengePoint(),
			data.displayStatus().getDescription(),
			data.createdDate()
		};
	}
}
