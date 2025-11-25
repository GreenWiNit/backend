package com.example.green.domain.pointshop.item.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.pointshop.item.entity.vo.ItemDisplayStatus;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자 아이템 목록 조회 응답")
public class PointItemSearchResponse {

	@Schema(description = "아이템 식별자", example = "1")
	private Long id;
	@Schema(description = "아이템 코드", example = "ITM-AA-001")
	private String code;
	@Schema(description = "아이템 이름", example = "맑은 뭉개 구름")
	private String name;
	@Schema(description = "아이템 가격", example = "1200")
	private BigDecimal pointPrice;
	@Schema(description = "아이템 재고 수량", example = "10")
	private Integer stockQuantity;
	@Schema(description = "아이템 판매 상태", example = "교환가능")
	private SellingStatus sellingStatus;
	@Schema(description = "아이템 전시 상태", example = "전시")
	private ItemDisplayStatus displayStatus;
	@Schema(description = "상품 등록 일자")
	private LocalDateTime createdDate;
}
