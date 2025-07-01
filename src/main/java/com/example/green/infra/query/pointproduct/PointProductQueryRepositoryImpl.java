package com.example.green.infra.query.pointproduct;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;

@Repository
public class PointProductQueryRepositoryImpl implements PointProductQueryRepository {
	@Override
	public List<PointProductSearchResponse> findTop10PointProducts(PointProductSearchCondition condition) {
		return List.of();
	}
}
