package com.example.green.domain.pointshop.controller.query;

import java.util.List;

import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;

public interface PointProductQueryRepository {
	List<PointProductSearchResponse> findTop10PointProducts(PointProductSearchCondition condition);
}
