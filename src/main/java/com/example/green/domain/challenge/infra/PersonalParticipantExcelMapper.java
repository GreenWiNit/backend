package com.example.green.domain.challenge.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.PersonalParticipationDto;
import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonalParticipantExcelMapper implements ExcelDataMapper<PersonalParticipationDto> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "personal_challenge_participant_" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<PersonalParticipationDto> getDataType() {
		return PersonalParticipationDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor headerColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("MemberKey", headerColor, FieldFormat.TEXT),
			ExcelField.of("참여한 날짜", headerColor, FieldFormat.DATE),
			ExcelField.of("인증 횟수", headerColor, FieldFormat.NUMBER)
		);
	}

	@Override
	public Object[] extractRowData(PersonalParticipationDto data) {
		return new Object[] {
			data.getMemberKey(),
			data.getParticipatingDate(),
			data.getCertCount()
		};
	}
}
