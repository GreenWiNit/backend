package com.example.green.domain.member.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.domain.member.repository.dto.UserBasicInfo;
import com.example.green.global.api.page.PageSearchCondition;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
import com.example.green.infra.database.querydsl.QueryPredicates;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

	private final QMember qMember = QMember.member;
	private final JPAQueryFactory jpaQueryFactory;
	private final MemberRepository memberRepository;

	@Override
	public PageTemplate<MemberPointsDto> searchMemberBasicInfo(BasicInfoSearchCondition condition) {
		BooleanExpression expression = BooleanExpressionConnector.combineWithOr(
			QueryPredicates.whenNotBlank(condition.keyword(), qMember.memberKey::containsIgnoreCase),
			QueryPredicates.whenNotBlank(condition.keyword(), qMember.profile.nickname::containsIgnoreCase)
		);

		Long totalCount = jpaQueryFactory.select(qMember.count())
			.from(qMember)
			.where(expression)
			.fetchOne();

		Pagination pagination = Pagination.fromCondition(condition, totalCount);

		List<MemberPointsDto> result = jpaQueryFactory.select(Projections.constructor(MemberPointsDto.class,
				qMember.id,
				qMember.memberKey,
				qMember.email,
				qMember.profile.nickname
			))
			.from(qMember)
			.where(expression)
			.orderBy(qMember.id.asc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
		return PageTemplate.of(result, pagination);
	}

	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));
	}

	@Override
	public List<Member> getMembers(List<Long> memberIds) {
		return memberRepository.findAllById(memberIds);
	}

	@Override
	public PageTemplate<UserBasicInfo> getUsersBasicInfo(PageSearchCondition condition) {
		BooleanExpression activeMembers = qMember.status.eq(MemberStatus.NORMAL)
			.and(qMember.deleted.eq(false));

		Long totalCount = jpaQueryFactory.select(qMember.count())
			.from(qMember)
			.where(activeMembers)
			.fetchOne();

		Pagination pagination = Pagination.fromCondition(condition, totalCount != null ? totalCount : 0);

		List<UserBasicInfo> result = jpaQueryFactory
			.select(Projections.constructor(UserBasicInfo.class,
				qMember.id,
				qMember.profile.nickname,
				qMember.profile.profileImageUrl
			))
			.from(qMember)
			.where(activeMembers)
			.orderBy(qMember.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}
}
