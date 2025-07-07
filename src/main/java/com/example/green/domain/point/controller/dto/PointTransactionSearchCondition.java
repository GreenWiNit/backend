package com.example.green.domain.point.controller.dto;

import com.example.green.global.api.page.PageSearchCondition;

public record PointTransactionSearchCondition(
	Integer page,
	Integer size
) implements PageSearchCondition {
}
