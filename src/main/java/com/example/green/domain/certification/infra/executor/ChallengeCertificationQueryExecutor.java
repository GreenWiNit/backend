package com.example.green.domain.certification.infra.executor;

import static com.example.green.domain.certification.domain.QChallengeCertification.*;
import static com.example.green.domain.certification.infra.projections.ChallengeCertificationProjections.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.infra.predicates.ChallengeCertificationPredicates;
import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChallengeCertificationQueryExecutor {

	private final JPAQueryFactory queryFactory;

	public List<ChallengeCertificationDto> executeCertByPersonalQuery(
		String cursor, Long memberId, Integer size, String type
	) {
		return queryFactory.select(toCertificationByPersonal())
			.from(challengeCertification)
			.where(ChallengeCertificationPredicates.certificationByPersonalCondition(memberId, cursor, type))
			.orderBy(challengeCertification.certifiedDate.desc(), challengeCertification.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public long executeSearchCountQuery(BooleanExpression expression) {
		return Optional.ofNullable(queryFactory.select(challengeCertification.count())
				.from(challengeCertification)
				.where(expression)
				.fetchOne())
			.orElseThrow(() -> new IllegalStateException("인증 검색 중 카운트 쿼리 예외 발생"));
	}

	public List<AdminCertificateSearchDto> executeSearchQuery(BooleanExpression expression, Pagination pagination) {
		return queryFactory.select(toSearch())
			.from(challengeCertification)
			.where(expression)
			.orderBy(challengeCertification.certifiedDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public Map<Long, Long> executeCertificationCountByMembersQuery(List<Long> memberIds) {
		return queryFactory.select(challengeCertification.member.memberId, challengeCertification.count())
			.from(challengeCertification)
			.where(challengeCertification.member.memberId.in(memberIds))
			.groupBy(challengeCertification.member.memberId)
			.transform(groupBy(challengeCertification.member.memberId).as(challengeCertification.count()));
	}
}
