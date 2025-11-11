package com.example.green.domain.pointshop.item.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 아이템 구매한 사용자 정보")
public record ItemWithApplicantsDTO(
	@Schema(description = "포인트 아이템 이름", example = "구름")
	String itemName, //상품명
	@Schema(description = "구매한 사용자 정보")
	List<BuyerInformation> buyers
) {
}
