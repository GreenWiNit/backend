package com.example.green.infra.client;

import java.util.List;
import java.util.Map;

import com.example.green.infra.client.dto.MemberDto;

public interface MemberClient {

	MemberDto getMember(Long memberId);

	List<MemberDto> getMembers(List<Long> memberIds);

	Map<Long, MemberDto> getMemberByIds(List<Long> memberIds);
}
