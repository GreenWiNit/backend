package com.example.green.domain.pointshop.controller.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class PointProductRequest {

	public static ApiTemplate<Long> create(PointProductCreateDto dto) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().post("/api/point-products")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static ApiTemplate<PageTemplate<PointProductSearchResponse>> searchProducts(
		PointProductSearchCondition condition
	) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.queryParam("page", condition.page())
			.queryParam("size", condition.size())
			.queryParam("keyword", condition.keyword())
			.queryParam("status", condition.status().name())
			.when().get("/api/point-products")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static void downloadExcel() {
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/point-products/excel")
			.then().log().all()
			.status(HttpStatus.OK);
	}
}
