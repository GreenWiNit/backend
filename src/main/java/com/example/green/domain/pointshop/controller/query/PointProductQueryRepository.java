package com.example.green.domain.pointshop.controller.query;

import java.util.List;

import com.example.green.domain.pointshop.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.dto.PointProductsView;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PointProductQueryRepository {

	PageTemplate<PointProductSearchResponse> searchPointProducts(PointProductSearchCondition condition);

	List<PointProductSearchResponse> searchPointProductsForExcel(PointProductExcelCondition condition);

	CursorTemplate<Long, PointProductsView> getProductsByCursor(Long cursor);
}
