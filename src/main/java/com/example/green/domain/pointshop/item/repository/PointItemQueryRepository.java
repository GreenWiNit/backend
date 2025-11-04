package com.example.green.domain.pointshop.item.repository;

import java.util.List;

import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.response.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PointItemQueryRepository {

	PageTemplate<PointItemSearchResponse> searchPointItems(PointItemSearchRequest pointItemSearchRequest);

	List<PointItemSearchResponse> searchPointItemsForExcel(PointItemExcelDownloadRequest pointItemExcelDownloadRequest);

	CursorTemplate<Long, PointItemResponse> getPointItemsByCursor(Long cursor);
}
