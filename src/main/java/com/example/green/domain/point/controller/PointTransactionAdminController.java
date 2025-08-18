package com.example.green.domain.point.controller;

import static com.example.green.domain.point.controller.message.PointTransactionResponseMessage.*;

import java.util.List;

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
import com.example.green.infra.excel.core.ExcelDownloader;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
@AdminApi
public class PointTransactionAdminController implements PointTransactionAdminControllerDocs {

	private final PointTransactionQueryRepository pointTransactionQueryRepository;
	private final ExcelDownloader excelDownloader;

	@GetMapping("/members/{memberId}")
	public ApiTemplate<PageTemplate<PointTransactionDto>> getPointsByMember(
		@PathVariable Long memberId,
		@ParameterObject @ModelAttribute PointTransactionSearchCondition condition
	) {
		PageTemplate<PointTransactionDto> result =
			pointTransactionQueryRepository.findPointTransactionByMember(memberId, condition);
		return ApiTemplate.ok(POINT_TRANSACTION_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/members/{memberId}/excel")
	public void exportPointTransactionExcel(
		@PathVariable Long memberId,
		HttpServletResponse response
	) {
		List<PointTransactionDto> result =
			pointTransactionQueryRepository.findPointTransactionByMemberForExcel(memberId);
		excelDownloader.downloadAsStream(result, response);
	}
}
