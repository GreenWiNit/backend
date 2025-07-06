package com.example.green.domain.point.controller.query;

import com.example.green.domain.point.controller.dto.MemberPointSummary;
import com.example.green.domain.point.controller.dto.MyPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.global.api.page.CursorTemplate;

public interface PointTransactionQueryRepository {

	MemberPointSummary findMemberPointSummary(Long memberId);

	CursorTemplate<Long, MyPointTransaction> getPointTransaction(Long memberId, Long cursor, TransactionType status);
}
