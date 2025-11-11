package com.example.green.domain.pointshop.item.repository;

public interface PointItemOrderQueryRepository {
	boolean existsByMemberIdAndPointItemId(Long memberId, Long pointItemId);
}
