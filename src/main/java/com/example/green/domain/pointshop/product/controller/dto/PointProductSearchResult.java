package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.pointshop.product.entity.vo.DisplayStatus;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자 상품 목록 조회 응답")
public class PointProductSearchResult {

	@Schema(description = "상품 식별자", example = "1")
	private Long id;
	@Schema(description = "상품 코드", example = "PRD-AA-001")
	private String code;
	@Schema(description = "상품명", example = "텀블러")
	private String name;
	@Schema(description = "상품 포인트 가격", example = "1000")
	private BigDecimal pointPrice;
	@Schema(description = "상품 재고 수량", example = "10")
	private Integer stockQuantity;
	@Schema(description = "상품 판매 상태", example = "교환가능")
	private SellingStatus sellingStatus;
	@Schema(description = "상품 전시 상태", example = "전시")
	private DisplayStatus displayStatus;
	@Schema(description = "상품 등록 일자")
	private LocalDateTime createdDate;
}
