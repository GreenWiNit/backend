package com.example.green.domain.pointshop.repository;

import com.example.green.domain.pointshop.repository.dto.ExchangeApplicationResult;
import com.example.green.domain.pointshop.repository.dto.ExchangeApplicationSearchCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductApplicantResult;
import com.example.green.global.api.page.PageTemplate;

public interface OrderQueryRepository {

	PageTemplate<PointProductApplicantResult> findExchangeApplicantByPointProduct(
		Long pointProductId,
		Integer page,
		Integer size
	);

	PageTemplate<ExchangeApplicationResult> searchExchangeApplication(ExchangeApplicationSearchCondition condition);
}
