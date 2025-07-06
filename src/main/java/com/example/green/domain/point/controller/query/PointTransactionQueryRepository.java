package com.example.green.domain.point.controller.query;

import com.example.green.domain.point.controller.dto.MemberPointSummary;

public interface PointTransactionQueryRepository {

	MemberPointSummary findMemberPointSummary(Long memberId);
}
