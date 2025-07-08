package com.example.green.domain.point.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.green.domain.point.controller.dto.PointTransactionSearchCondition;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PointTransactionQueryRepository {

	MemberPointSummary findMemberPointSummary(Long memberId);

	CursorTemplate<Long, MyPointTransactionDto> getPointTransaction(Long memberId, Long cursor, TransactionType status);

	Map<Long, BigDecimal> findEarnedPointByMember(List<Long> memberIds);

	PageTemplate<PointTransactionDto> findPointTransactionByMember(
		Long memberId,
		PointTransactionSearchCondition condition
	);

	List<PointTransactionDto> findPointTransactionByMemberForExcel(Long memberId);
}
