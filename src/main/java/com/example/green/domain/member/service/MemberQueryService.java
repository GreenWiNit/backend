package com.example.green.domain.member.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

	private final PointClient pointClient;
	private final MemberQueryRepository memberQueryRepository;

	public PageTemplate<MemberPointsDto> searchMembersPoint(BasicInfoSearchCondition condition) {
		PageTemplate<MemberPointsDto> page = memberQueryRepository.searchMemberBasicInfo(condition);

		List<Long> memberIds = convertMemberIds(page);
		Map<Long, BigDecimal> earnedPointByMember = pointClient.getEarnedPointByMember(memberIds);

		page.content().forEach(member ->
			member.setMemberPoint(earnedPointByMember.get(member.getMemberId()))
		);
		return page;
	}

	private static List<Long> convertMemberIds(PageTemplate<MemberPointsDto> page) {
		List<Long> memberIds = page.content()
			.stream()
			.map(MemberPointsDto::getMemberId)
			.toList();
		return memberIds;
	}
}
