package com.example.green.domain.certification.infra.predicates;

import static com.example.green.domain.certification.domain.QChallengeCertification.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.domain.certification.infra.filter.ChallengeCertificationFilter;
import com.example.green.infra.database.utils.BooleanExpressionConnector;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeCertificationPredicates {

	public static BooleanExpression certificationByPersonalCondition(Long memberId, String cursor, String type) {
		return BooleanExpressionConnector.combineConditions(
			certificationByPersonalCursorCondition(cursor),
			challengeCertification.member.memberId.eq(memberId),
			challengeCertification.challenge.type.eq(type)
		);
	}

	public static BooleanExpression searchCondition(ChallengeCertificationFilter filter) {
		return BooleanExpressionConnector.combineConditions(
			challengeCertification.challenge.type.eq(filter.type()),
			statusCondition(filter.status()),
			memberKeyCondition(filter.memberKey()),
			challengeNameCondition(filter.challengeName()),
			groupCodeCondition(filter.groupCode())
		);
	}

	private static BooleanExpression certificationByPersonalCursorCondition(String cursor) {
		if (cursor == null) {
			return null;
		}
		String[] parts = cursor.split(",");
		LocalDate dateCursor = LocalDate.parse(parts[0]);
		Long idCursor = Long.parseLong(parts[1]);
		return challengeCertification.certifiedDate.lt(dateCursor)
			.or(challengeCertification.certifiedDate.eq(dateCursor)
				.and(challengeGroup.id.lt(idCursor)));
	}

	private static BooleanExpression statusCondition(CertificationStatus status) {
		if (status == null) {
			return null;
		}
		return challengeCertification.status.eq(status);
	}

	private static BooleanExpression memberKeyCondition(String memberKey) {
		if (memberKey == null || memberKey.isBlank()) {
			return null;
		}
		return challengeCertification.member.memberKey.containsIgnoreCase(memberKey);
	}

	private static BooleanExpression challengeNameCondition(String challengeName) {
		if (challengeName == null || challengeName.isBlank()) {
			return null;
		}
		return challengeCertification.challenge.challengeName.containsIgnoreCase(challengeName);
	}

	private static BooleanExpression groupCodeCondition(String groupCode) {
		if (groupCode == null || groupCode.isBlank()) {
			return null;
		}
		return challengeCertification.challenge.groupCode.containsIgnoreCase(groupCode);
	}
}
