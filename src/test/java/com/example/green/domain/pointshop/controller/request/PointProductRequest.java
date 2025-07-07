package com.example.green.domain.pointshop.controller.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductDetail;
import com.example.green.domain.pointshop.controller.dto.PointProductUpdateDto;
import com.example.green.domain.pointshop.repository.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.repository.dto.PointProductView;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class PointProductRequest {

	public static ApiTemplate<Long> create(PointProductCreateDto dto) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().post("/api/admin/point-products")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static ApiTemplate<PageTemplate<PointProductSearchResponse>> searchProducts() {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/admin/point-products")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static NoContent update(PointProductUpdateDto dto) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().put("/api/admin/point-products/1")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static void downloadExcel() {
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/admin/point-products/excel")
			.then().log().all()
			.status(HttpStatus.OK);
	}

	public static NoContent delete(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/admin/point-products/" + id)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static NoContent show(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().patch("/api/admin/point-products/" + id + "/show")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static NoContent hide(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().patch("/api/admin/point-products/" + id + "/hide")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static ApiTemplate<CursorTemplate<Long, PointProductView>> getProducts(Long cursor) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.param("cursor", cursor)
			.when().get("/api/point-products")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	public static ApiTemplate<PointProductDetail> getProductById(Long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/point-products/" + id)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}
}
