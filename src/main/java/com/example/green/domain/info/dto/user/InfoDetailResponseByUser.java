package com.example.green.domain.info.dto.user;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 사용자 정보 상세
 * */
public record InfoDetailResponseByUser(
	String id,
	String infoCategoryName, // @JsonValue로 Json직렬화 단계 확인 가능 but 명시적인 방법 채택
	String title

) {
	public static InfoDetailResponseByUser from(InfoEntity e) {
		return new InfoDetailResponseByUser(
			e.getInfoCategory().getDescription(),
			e.getId(),
			e.getTitle()
		);
	}
}
