package com.example.green.domain.info.dto.admin;

import java.util.List;

import com.example.green.domain.info.dto.InfoPage;

/**
 * 관리자 전체 목록 조회 wrapper
 * */
public record AdminInfoSearchListResponse(
	List<AdminInfoSearchResponse> content,
	InfoPage page

) {
}
