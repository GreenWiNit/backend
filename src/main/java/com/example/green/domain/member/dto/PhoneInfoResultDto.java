package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PhoneInfoResultDto {
	private final Member member;
	private final boolean authenticated;
}
