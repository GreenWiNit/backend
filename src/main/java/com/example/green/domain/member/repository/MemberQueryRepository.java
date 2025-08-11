package com.example.green.domain.member.repository;

import java.util.List;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.page.PageTemplate;

public interface MemberQueryRepository {

	PageTemplate<MemberPointsDto> searchMemberBasicInfo(BasicInfoSearchCondition condition);

	Member getMember(Long memberId);

	List<Member> getMembers(List<Long> memberIds);
}
