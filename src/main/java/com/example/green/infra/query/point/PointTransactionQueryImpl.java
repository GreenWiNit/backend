package com.example.green.infra.query.point;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.point.controller.dto.MemberPointSummary;
import com.example.green.domain.point.controller.query.PointTransactionQueryRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointTransactionQueryImpl implements PointTransactionQueryRepository {

	private final EntityManager entityManager;

	@Override
	public MemberPointSummary findMemberPointSummary(Long memberId) {
		return entityManager.createQuery("""
				SELECT new com.example.green.domain.point.controller.dto.MemberPointSummary(
				    COALESCE(
				        (SELECT pt2.balanceAfter.amount 
				         FROM PointTransaction pt2 
				         WHERE pt2.memberId = :memberId 
				         ORDER BY pt2.id DESC 
				         LIMIT 1), 0
				    ),
				    COALESCE(SUM(CASE WHEN pt.type = 'EARN' THEN pt.pointAmount.amount ELSE 0 END), 0),
				    COALESCE(SUM(CASE WHEN pt.type = 'SPEND' THEN pt.pointAmount.amount ELSE 0 END), 0)
				)
				FROM PointTransaction pt 
				WHERE pt.memberId = :memberId
				""", MemberPointSummary.class)
			.setParameter("memberId", memberId)
			.getSingleResult();
	}
}
