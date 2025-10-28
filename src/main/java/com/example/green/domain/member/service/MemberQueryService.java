package com.example.green.domain.member.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.dto.UserSummaryDto;
import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.domain.member.repository.dto.UserBasicInfo;
import com.example.green.global.api.page.PageSearchCondition;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.example.green.infra.client.ChallengeClient;
import com.example.green.infra.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

	private final PointClient pointClient;
	private final ChallengeClient challengeClient;
	private final MemberQueryRepository memberQueryRepository;

	public PageTemplate<MemberPointsDto> searchMembersPoint(BasicInfoSearchCondition condition) {
		PageTemplate<MemberPointsDto> page = memberQueryRepository.searchMemberBasicInfo(condition);

		List<Long> memberIds = page.content().stream().map(MemberPointsDto::getMemberId).toList();
		Map<Long, BigDecimal> earnedPointByMember = pointClient.getEarnedPointByMember(memberIds);
		page.content().forEach(member -> member.setMemberPoint(earnedPointByMember.get(member.getMemberId())));

		return page;
	}

	public PageTemplate<UserSummaryDto> getUsersSummary(Integer page, Integer size, Long currentMemberId) {
		PageSearchCondition condition = new PageSearchCondition() {
			@Override
			public Integer page() {
				return page;
			}

			@Override
			public Integer size() {
				return size;
			}
		};

		PageTemplate<UserBasicInfo> basicPage = memberQueryRepository.getUsersBasicInfo(condition);

		if (basicPage.content().isEmpty()) {
			return new PageTemplate<>(
				basicPage.totalElements(),
				basicPage.totalPages(),
				basicPage.currentPage(),
				basicPage.pageSize(),
				basicPage.hasNext(),
				List.of()
			);
		}

		List<Long> memberIds = basicPage.content().stream()
			.map(UserBasicInfo::memberId)
			.toList();

		Map<Long, BigDecimal> pointsMap = pointClient.getEarnedPointByMember(memberIds);
		Map<Long, Long> certCountMap = challengeClient.getCertificationCountByMembers(memberIds);

		List<UserSummaryDto> result = basicPage.content().stream()
			.map(info -> UserSummaryDto.of(
				info.memberId(),
				info.nickname(),
				info.profileImageUrl(),
				currentMemberId,
				certCountMap.getOrDefault(info.memberId(), 0L),
				pointsMap.getOrDefault(info.memberId(), BigDecimal.ZERO)
			))
			.toList();

		Pagination pagination = Pagination.of(
			basicPage.totalElements(),
			basicPage.currentPage(),
			basicPage.pageSize()
		);

		return PageTemplate.of(result, pagination);
	}
}
