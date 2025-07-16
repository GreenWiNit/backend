package com.example.green.domain.pointshop.product.repository;

import java.util.List;

import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PointProductQueryRepository {

	PageTemplate<PointProductSearchResult> searchPointProducts(PointProductSearchCondition condition);

	List<PointProductSearchResult> searchPointProductsForExcel(PointProductExcelCondition condition);

	CursorTemplate<Long, PointProductView> getProductsByCursor(Long cursor);
}
