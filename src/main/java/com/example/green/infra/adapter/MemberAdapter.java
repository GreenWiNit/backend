package com.example.green.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.global.client.MemberClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberAdapter implements MemberClient {

	private final MemberQueryRepository memberQuery;

	public String getMemberKey(Long memberId) {
		Member member = memberQuery.getMember(memberId);
		return member.getMemberKey();
	}

	public List<String> getMemberKeys(List<Long> memberIds) {
		return memberQuery.getMembers(memberIds)
			.stream()
			.map(Member::getMemberKey)
			.toList();
	}
}
