package com.example.green.domain.info.dto;

/**
 * 전체 페이지 조회
 * - totalElements: 전체 게시글 수
 * - totalPages: 전체 페이지 수
 * */
public record InfoPage(
	long totalElements,
	int totalPages
) {
}
