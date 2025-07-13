package com.example.green.domain.member.excel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.member.dto.admin.MemberListResponseDto;
import com.example.green.global.excel.core.ExcelDataMapper;
import com.example.green.global.excel.style.BackGroundColor;
import com.example.green.global.excel.style.ExcelField;
import com.example.green.global.excel.style.FieldFormat;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberExcelMapper implements ExcelDataMapper<MemberListResponseDto> {

	private final TimeUtils timeUtils;

	@Override
	public String getFileName() {
		return "member_list_" + timeUtils.getFormattedDate("yyyyMMddHHmmss");
	}

	@Override
	public Class<MemberListResponseDto> getDataType() {
		return MemberListResponseDto.class;
	}

	@Override
	public List<ExcelField> getFields() {
		BackGroundColor headerColor = BackGroundColor.LIGHT_GRAY;
		return List.of(
			ExcelField.of("사용자명", headerColor, FieldFormat.TEXT),
			ExcelField.of("이메일", headerColor, FieldFormat.TEXT),
			ExcelField.of("닉네임", headerColor, FieldFormat.TEXT),
			ExcelField.of("전화번호", headerColor, FieldFormat.TEXT),
			ExcelField.of("가입일", headerColor, FieldFormat.DATE),
			ExcelField.of("등급", headerColor, FieldFormat.TEXT),
			ExcelField.of("소셜 제공자", headerColor, FieldFormat.TEXT)
		);
	}

	@Override
	public Object[] extractRowData(MemberListResponseDto data) {
		return new Object[] {
			data.memberKey(),
			data.email(),
			data.nickname(),
			data.phoneNumber() != null ? data.phoneNumber() : "-",
			data.joinDate(),
			data.role(),
			data.provider()
		};
	}

	@Override
	public String getSheetName() {
		return "회원목록";
	}
} 