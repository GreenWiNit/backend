package com.example.green.domain.info.dto.user;

import java.util.List;

/**
 * 사용자 전체 목록 조회
 * */
public record InfoSearchListResponseByUser(
	List<InfoSearchResponseByUser> content
) {
}
