package com.example.green.domain.pointshop.item.repository;

import com.example.green.domain.pointshop.item.dto.response.ItemWithApplicantsDTO;
import com.example.green.global.api.page.PageTemplate;

public interface PointItemOrderQueryRepository {

	boolean existsByMemberIdAndPointItemId(Long memberId, Long pointItemId);

	PageTemplate<ItemWithApplicantsDTO> findAllItemsWithApplicants(Integer page, Integer size);
}
