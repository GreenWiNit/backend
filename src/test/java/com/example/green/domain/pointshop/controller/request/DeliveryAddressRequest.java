package com.example.green.domain.pointshop.controller.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.green.domain.pointshop.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.service.result.DeliveryResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class DeliveryAddressRequest {

	public static ApiTemplate<Long> create(DeliveryAddressCreateDto dto) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().post("/api/deliveries/addresses")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static NoContent update(DeliveryAddressUpdateDto dto) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().put("/api/deliveries/addresses/1")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static ApiTemplate<DeliveryResult> get() {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/deliveries/addresses")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}
}
