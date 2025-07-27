package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

/**
 * 휴대폰 정보 조회 결과를 담는 DTO
 * 서비스 계층에서 컨트롤러로 데이터를 전달하는 용도
 */
public class PhoneInfoResultDto {
	private final Member member;
	private final boolean isAuthenticated;

	public PhoneInfoResultDto(Member member, boolean isAuthenticated) {
		this.member = member;
		this.isAuthenticated = isAuthenticated;
	}

	public Member getMember() {
		return member;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}
} 