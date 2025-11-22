package com.example.green.domain.pointshop.item.controller;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.item.dto.request.CreatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.request.UpdatePointItemRequest;

public class DummyData {

	public static CreatePointItemRequest createPointItemRequest() {
		return new CreatePointItemRequest(
			"ITM-AA-001",
			"맑은 뭉게 구름",
			"하늘에서 포근한 구름이 내려와 식물을 감싸요. 몽글몽글 기분 좋은 하루!",
			"https://thumbnail.url/rainbow-pot.jpg",
			BigDecimal.valueOf(450),
			10
		);
	}

	public static UpdatePointItemRequest updatePointItemRequest() {
		return new UpdatePointItemRequest(
			"ITM-AA-002",
			"행운의 네잎클로버",
			"행운의 네잎클로버가 싱그럽게 피어나요. 오늘 하루도 행운 가득!",
			"https://thumbnail.url/clover.jpg",
			BigDecimal.valueOf(600),
			20
		);
	}

}
