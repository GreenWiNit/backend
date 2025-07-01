package com.example.green.domain.pointshop.controller.query;

import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.global.api.page.PageTemplate;

public interface PointProductQueryRepository {

	PageTemplate<PointProductSearchResponse> searchPointProducts(PointProductSearchCondition condition);
}
