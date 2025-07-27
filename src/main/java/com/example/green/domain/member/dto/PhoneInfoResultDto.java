package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

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