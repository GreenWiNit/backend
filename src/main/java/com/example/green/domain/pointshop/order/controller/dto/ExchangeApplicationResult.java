package com.example.green.domain.pointshop.order.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.OrderDeliveryStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 교환 신청 결과")
public class ExchangeApplicationResult {

	@Schema(description = "상품 교환 내역 식별자", example = "1")
	private long id;
	@Schema(description = "상품 교환 신청일자")
	private LocalDateTime exchangedAt;
	@Schema(description = "상품 교환 신청자 키 값", example = "oauth_0000")
	private String memberKey;
	@Schema(description = "상품 교환 신청자 이메일", example = "greenwinit@gmail.com")
	private String memberEmail;
	@Schema(description = "상품 코드", example = "PRD-AA-001")
	private String pointProductCode;
	@Schema(description = "상품 구매 수량", example = "5")
	private int quantity;
	@Schema(description = "상품 구매 총 가격", example = "10000")
	private BigDecimal totalPrice;
	@Schema(description = "상품 수령자 이름", example = "홍길동")
	private String recipientName;
	@Schema(description = "상품 수령자 연락처", example = "010-1234-5678")
	private String recipientPhoneNumber;
	@Schema(description = "상품 수령자 전체 주소", example = "도로명 (우편번호), 상세주소")
	private String fullAddress;
	@Schema(description = "상품 주문 처리 상태")
	private OrderDeliveryStatus status;

	public ExchangeApplicationResult(
		Long id,
		LocalDateTime exchangedAt,
		String memberKey,
		String memberEmail,
		String pointProductCode,
		int quantity,
		BigDecimal totalPrice,
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		OrderDeliveryStatus status
	) {
		this(id,
			exchangedAt,
			memberKey,
			memberEmail,
			pointProductCode,
			quantity,
			totalPrice,
			deliveryAddressSnapshot.getRecipientName(),
			deliveryAddressSnapshot.getPhoneNumber(),
			deliveryAddressSnapshot.getFullAddress(),
			status);
	}
}
