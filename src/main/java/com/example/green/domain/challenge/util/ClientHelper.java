package com.example.green.domain.challenge.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.global.client.MemberClient;
import com.example.green.global.client.dto.MemberDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientHelper {

	private final MemberClient memberClient;

	public String requestMemberKey(Long memberId) {
		return memberClient.getMember(memberId).getMemberKey();
	}

	public List<String> requestMemberKeys(List<Long> memberIds) {
		return memberClient.getMembers(memberIds)
			.stream()
			.map(MemberDto::getMemberKey)
			.toList();
	}
}
