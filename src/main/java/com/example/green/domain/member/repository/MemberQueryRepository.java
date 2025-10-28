package com.example.green.domain.member.repository;

import java.util.List;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.domain.member.repository.dto.UserBasicInfo;
import com.example.green.global.api.page.PageSearchCondition;
import com.example.green.global.api.page.PageTemplate;

public interface MemberQueryRepository {

	PageTemplate<MemberPointsDto> searchMemberBasicInfo(BasicInfoSearchCondition condition);

	Member getMember(Long memberId);

	List<Member> getMembers(List<Long> memberIds);

	/**
	 * 사용자 기본 정보 목록 조회 (NORMAL 상태, deleted=false만)
	 * @param condition 페이지 검색 조건
	 * @return 사용자 기본 정보 페이지
	 */
	PageTemplate<UserBasicInfo> getUsersBasicInfo(PageSearchCondition condition);
}
