package com.example.green.template.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.green.global.api.ApiTemplate;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class ImageUploadRequest {

	public static ApiTemplate<String> upload(String filename, byte[] bytes, String contentType, String purpose) {
		return RestAssuredMockMvc
			.given().log().all()
			//.header("Authorization", "Bearer token")
			//.cookie("refreshToken", "test-refresh-token")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.multiPart("imageFile", filename, bytes, contentType)
			.param("purpose", purpose)
			.when().post("/api/images")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}
}
