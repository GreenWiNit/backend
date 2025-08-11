package com.example.green.infra.query.member;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberQueryRepository;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.example.green.global.error.exception.BusinessException;
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
		Long totalCount = jpaQueryFactory.select(qMember.count())
			.from(qMember)
			.where(
				fromEmail(condition.keyword()),
				fromName(condition.keyword())
			)
			.fetchOne();

		Pagination pagination = Pagination.fromCondition(condition, totalCount);

		List<MemberPointsDto> result = jpaQueryFactory.select(Projections.constructor(MemberPointsDto.class,
				qMember.id,
				qMember.email,
				qMember.name
			))
			.from(qMember)
			.where(
				fromEmail(condition.keyword()),
				fromName(condition.keyword())
			)
			// 정렬 기준 따로 없음
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

	private BooleanExpression fromEmail(String email) {
		if (email == null) {
			return null;
		}
		return qMember.email.containsIgnoreCase(email);
	}

	private BooleanExpression fromName(String nickname) {
		if (nickname == null) {
			return null;
		}
		return qMember.name.containsIgnoreCase(nickname);
	}
}
