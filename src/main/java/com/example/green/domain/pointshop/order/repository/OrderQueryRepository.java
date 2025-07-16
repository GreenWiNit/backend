package com.example.green.domain.pointshop.order.repository;

import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationResult;
import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationSearchCondition;
import com.example.green.domain.pointshop.order.controller.dto.PointProductApplicantResult;
import com.example.green.global.api.page.PageTemplate;

public interface OrderQueryRepository {

	PageTemplate<PointProductApplicantResult> findExchangeApplicantByPointProduct(
		Long pointProductId,
		Integer page,
		Integer size
	);

	PageTemplate<ExchangeApplicationResult> searchExchangeApplication(ExchangeApplicationSearchCondition condition);
}
