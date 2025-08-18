package com.example.green.domain.certification.ui.dto.sub;

import com.example.green.domain.certification.domain.MemberSnapshot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberInfo {
	private Long id;
	private String key;

	public static MemberInfo from(MemberSnapshot snapshot) {
		return new MemberInfo(
			snapshot.getMemberId(),
			snapshot.getMemberKey()
		);
	}
}