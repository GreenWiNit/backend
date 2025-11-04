package com.example.green.domain.pointshop.item.infra;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.response.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointItemQueryRepositoryImpl implements PointItemQueryRepository {

	private static final int DEFAULT_CURSOR_VIEW_SIZE = 20;
	private final PointItemQueryExecutor pointItemQueryExecutor;

	@Override
	public PageTemplate<PointItemSearchResponse> searchPointItems(PointItemSearchRequest pointItemSearchRequest) {

		BooleanExpression expression = PointItemPredicates.fromCondition(pointItemSearchRequest.keyword());

		Long totalCount = pointItemQueryExecutor.countItems(expression);
		Pagination pagination = Pagination.fromCondition(pointItemSearchRequest, totalCount);

		List<PointItemSearchResponse> pointItem = pointItemQueryExecutor.findItems(expression, pagination);

		return PageTemplate.of(pointItem, pagination);
	}

	@Override
	public List<PointItemSearchResponse> searchPointItemsForExcel(
		PointItemExcelDownloadRequest pointItemExcelDownloadRequest) {
		return List.of();
	}

	@Override
	public CursorTemplate<Long, PointItemResponse> getPointItemsByCursor(Long cursor) {
		BooleanExpression cursorContents = PointItemPredicates.fromCursorCondition(cursor);

		List<PointItemResponse> pointItemResponse =
			pointItemQueryExecutor.findItemByCursor(cursorContents, DEFAULT_CURSOR_VIEW_SIZE);

		boolean hasNext = pointItemResponse.size() > DEFAULT_CURSOR_VIEW_SIZE;
		if (!hasNext) {
			return CursorTemplate.of(pointItemResponse);
		}

		pointItemResponse.removeLast();

		if (pointItemResponse.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		return CursorTemplate.ofWithNextCursor(pointItemResponse.getLast().pointItemId(), pointItemResponse);
	}
}
