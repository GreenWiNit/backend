package com.example.green.domain.pointshop.repository;

import java.util.List;

import com.example.green.domain.pointshop.repository.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.repository.dto.PointProductView;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PointProductQueryRepository {

	PageTemplate<PointProductSearchResponse> searchPointProducts(PointProductSearchCondition condition);

	List<PointProductSearchResponse> searchPointProductsForExcel(PointProductExcelCondition condition);

	CursorTemplate<Long, PointProductView> getProductsByCursor(Long cursor);
}
