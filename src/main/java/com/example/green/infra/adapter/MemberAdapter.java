package com.example.green.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.global.client.MemberClient;
import com.example.green.global.client.dto.MemberDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberAdapter implements MemberClient {

	private final MemberQueryRepository memberQuery;

	public MemberDto getMember(Long memberId) {
		return MemberDto.from(memberQuery.getMember(memberId));
	}

	public List<MemberDto> getMembers(List<Long> memberIds) {
		return memberQuery.getMembers(memberIds)
			.stream()
			.map(MemberDto::from)
			.toList();
	}

	public Map<Long, MemberDto> getMemberByIds(List<Long> memberIds) {
		return getMembers(memberIds)
			.stream()
			.collect(Collectors.toMap(MemberDto::getId, Function.identity()));
	}
}
