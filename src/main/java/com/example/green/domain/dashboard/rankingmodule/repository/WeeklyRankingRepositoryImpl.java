package com.example.green.domain.dashboard.rankingmodule.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.entity.challenge.QBaseChallengeParticipation;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WeeklyRankingRepositoryImpl implements WeeklyRankingRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Tuple> findTopMembersOfWeek(LocalDate weekStart, int topN) {

		QMember member = QMember.member;
		QPointTransaction point = QPointTransaction.pointTransaction;
		QBaseChallengeParticipation challenge = QBaseChallengeParticipation.baseChallengeParticipation;

		LocalDateTime startDateTime = weekStart.atStartOfDay();
		LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

		return queryFactory
			.select(
				member.id,
				member.name,
				point.pointAmount.amount.sum(),
				challenge.certCount.sum().coalesce(0)
			)
			.from(member)
			.leftJoin(point).on(
				point.memberId.eq(member.id)
					.and(point.type.eq(TransactionType.EARN))
					.and(point.createdDate.between(startDateTime, endDateTime))
			)
			.leftJoin(challenge).on(
				challenge.memberId.eq(member.id)
					.and(challenge.participatedAt.between(startDateTime, endDateTime))
			)
			.groupBy(member.id, member.name)
			.orderBy(
				point.pointAmount.amount.sum().desc(),      // 총 포인트 우선
				challenge.certCount.sum().desc()            // 동점 시 챌린지 수 우선
			)
			.limit(topN)
			.fetch();
		
	}
}
