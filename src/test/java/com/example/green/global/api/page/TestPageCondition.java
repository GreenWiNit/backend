package com.example.green.global.api.page;

public record TestPageCondition(
	Integer page,
	Integer size
) implements PageSearchCondition {
}
