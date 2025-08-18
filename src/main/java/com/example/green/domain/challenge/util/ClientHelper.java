package com.example.green.domain.challenge.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.green.infra.client.MemberClient;
import com.example.green.infra.client.dto.MemberDto;

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

	public Map<Long, String> requestMemberKeyById(List<Long> memberIds) {
		return memberClient.getMemberByIds(memberIds)
			.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getMemberKey()));
	}
}
