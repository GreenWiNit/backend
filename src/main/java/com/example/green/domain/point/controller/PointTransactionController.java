package com.example.green.domain.point.controller;

import static com.example.green.domain.point.controller.message.PointTransactionResponseMessage.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.point.controller.docs.PointTransactionControllerDocs;
import com.example.green.domain.point.controller.dto.MemberPointSummary;
import com.example.green.domain.point.controller.query.PointTransactionQueryRepository;
import com.example.green.global.api.ApiTemplate;

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
		return ApiTemplate.ok(POINT_TRANSACTION_INQUIRY_SUCCESS, result);
	}
}
