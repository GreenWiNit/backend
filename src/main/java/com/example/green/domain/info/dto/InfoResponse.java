package com.example.green.domain.info.dto;

import lombok.Builder;

/**
 * create/update 한 페이지에서 사용하기 때문에 Response 성격을 구분하지 않음
 * */
@Builder
public record InfoResponse() {
}
