package com.example.green.global.client.dto;

import com.example.green.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

	private Long id;
	private String memberKey;
	private String name;
	private String email;
	private String phoneNumber;

	public static MemberDto from(Member member) {
		return new MemberDto(
			member.getId(),
			member.getMemberKey(),
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber()
		);
	}
}
