package com.example.green.domain.point.controller;

import static com.example.green.domain.point.controller.message.PointTransactionResponseMessage.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.point.controller.docs.PointTransactionAdminControllerDocs;
import com.example.green.domain.point.controller.dto.PointTransactionSearchCondition;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
public class PointTransactionAdminController implements PointTransactionAdminControllerDocs {

	private final PointTransactionQueryRepository pointTransactionQueryRepository;

	@GetMapping("/members/{memberId}")
	public ApiTemplate<PageTemplate<PointTransactionDto>> getPointsByMember(
		@PathVariable Long memberId,
		@ParameterObject @ModelAttribute PointTransactionSearchCondition condition
	) {
		PageTemplate<PointTransactionDto> result =
			pointTransactionQueryRepository.findPointTransactionByMember(memberId, condition);
		return ApiTemplate.ok(POINT_TRANSACTION_INQUIRY_SUCCESS, result);
	}
}
