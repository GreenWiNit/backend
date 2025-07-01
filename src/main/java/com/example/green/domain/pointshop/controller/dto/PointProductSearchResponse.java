package com.example.green.domain.pointshop.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.pointshop.entity.pointproduct.vo.DisplayStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointProductSearchResponse {

	private String code;
	private String name;
	private BigDecimal pointPrice;
	private Integer stockQuantity;
	private SellingStatus sellingStatus;
	private DisplayStatus displayStatus;
	private LocalDateTime createdAt;
}
