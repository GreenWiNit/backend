package com.example.green.domain.challenge.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TeamParticipantExcelMapper implements ExcelDataMapper<AdminTeamParticipantDto> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "team_challenge_participant_" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<AdminTeamParticipantDto> getDataType() {
		return AdminTeamParticipantDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor headerColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("팀 코드", headerColor, FieldFormat.TEXT),
			ExcelField.of("MemberKey", headerColor, FieldFormat.TEXT),
			ExcelField.of("참여한 날짜", headerColor, FieldFormat.DATE),
			ExcelField.of("그룹 참여 날짜", headerColor, FieldFormat.DATE)
		);
	}

	@Override
	public Object[] extractRowData(AdminTeamParticipantDto data) {
		return new Object[] {
			data.getGroupCode(),
			data.getMemberKey(),
			data.getParticipatingDate(),
			data.getGroupParticipatingDate()
		};
	}
}
