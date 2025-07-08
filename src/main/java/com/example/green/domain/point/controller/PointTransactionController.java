package com.example.green.domain.point.controller;

import static com.example.green.domain.point.controller.message.PointTransactionResponseMessage.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.point.controller.docs.PointTransactionControllerDocs;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointTransactionController implements PointTransactionControllerDocs {

	private final PointTransactionQueryRepository pointTransactionQueryRepository;

	@GetMapping("/me")
	public ApiTemplate<MemberPointSummary> getPointSummary() {
		//Long memberId = principalDetails.getMemberId();
		MemberPointSummary result = pointTransactionQueryRepository.findMemberPointSummary(1L);
		return ApiTemplate.ok(MY_POINT_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/transaction")
	public ApiTemplate<CursorTemplate<Long, MyPointTransactionDto>> getMyPointTransaction(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false) TransactionType status
	) {
		// todo: security 가져와야 함
		Long memberId = 1L;
		CursorTemplate<Long, MyPointTransactionDto> result =
			pointTransactionQueryRepository.getPointTransaction(memberId, cursor, status);
		return ApiTemplate.ok(POINT_TRANSACTION_INQUIRY_SUCCESS, result);
	}
}
