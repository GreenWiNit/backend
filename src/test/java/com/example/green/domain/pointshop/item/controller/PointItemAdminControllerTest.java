package com.example.green.domain.pointshop.item.controller;

import static com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.item.dto.request.CreatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.request.UpdatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.domain.pointshop.item.service.PointItemQueryService;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.infra.excel.core.ExcelDownloader;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(PointItemAdminController.class)
class PointItemAdminControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointItemService pointItemService;

	@MockitoBean
	private PointItemQueryService pointItemQueryService;

	@MockitoBean
	private PointItemQueryRepository pointItemQueryRepository;

	@MockitoBean
	private ExcelDownloader excelDownloader;

	@Test
	void 포인트_아이템_생성_요청에_성공한다() {
		//given
		CreatePointItemRequest createPointItemRequest = DummyData.createPointItemRequest();
		when(pointItemService.create(any(PointItemCreateCommand.class))).thenReturn(1L);

		//when
		ApiTemplate<Long> response = create(createPointItemRequest);

		//then
		assertThat(response.result()).isEqualTo(1L);
		assertThat(response.message()).isEqualTo(POINT_ITEM_CREATION_SUCCESS.getMessage());
	}

	public static ApiTemplate<Long> create(CreatePointItemRequest createPointItemRequest) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(createPointItemRequest)
			.when().post("/api/admin/point-items")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트_아이템_목록_조회_요청에_성공한다() {
		//given
		PointItemSearchResponse data = mock(PointItemSearchResponse.class);
		PageTemplate<PointItemSearchResponse> dataResult = new PageTemplate<>(0, 0, 0, 0, false, List.of(data));
		when(pointItemQueryRepository.searchPointItems(any(PointItemSearchRequest.class))).thenReturn(dataResult);

		//when & then
		ApiTemplate<PageTemplate<PointItemSearchResponse>> response = searchItems();

		assertThat(response.result()).usingRecursiveComparison().isEqualTo(dataResult);
		assertThat(response.message()).isEqualTo(
			POINT_ITEMS_INQUIRY_SUCCESS.getMessage()
		);

	}

	public static ApiTemplate<PageTemplate<PointItemSearchResponse>> searchItems() {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/admin/point-items")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트_아이템_수정_요청에_성공한다() {
		//given
		UpdatePointItemRequest updatePointItemRequest = DummyData.updatePointItemRequest();

		// when
		NoContent response = update(updatePointItemRequest);

		// then
		assertThat(response.message()).isEqualTo(POINT_ITEM_UPDATE_SUCCESS.getMessage());
		verify(pointItemService).updatePointItem(any(PointItemUpdateCommand.class), anyLong());

	}

	public static NoContent update(UpdatePointItemRequest updatePointItemRequest) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(updatePointItemRequest)
			.when().put("/api/admin/point-items/1")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트_아이템_삭제_요청_성공한다() {
		//when
		NoContent deleteResponse = delete(1L);
		assertThat(deleteResponse.message()).isEqualTo(POINT_ITEM_DELETE_SUCCESS.getMessage());

	}

	public static NoContent delete(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/admin/point-items/" + id)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트_아이템_전시_요청_성공한다() {
		//when
		NoContent deleteResponse = show(1L);
		assertThat(deleteResponse.message()).isEqualTo(DISPLAY_SHOW_ITEM_SUCCESS.getMessage());

	}

	public static NoContent show(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().patch("/api/admin/point-items/" + id + "/show")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트_아이템_미전시_요청_성공한다() {
		//when
		NoContent deleteResponse = hide(1L);
		assertThat(deleteResponse.message()).isEqualTo(DISPLAY_HIDE_ITEM_SUCCESS.getMessage());

	}

	public static NoContent hide(long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().patch("/api/admin/point-items/" + id + "/hide")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 엑셀_다운로드에_성공한다() {
		// given
		List<PointItemSearchResponse> mockResult = List.of(mock(PointItemSearchResponse.class));
		when(pointItemQueryRepository.searchPointItemsForExcel(any(PointItemExcelDownloadRequest.class)))
			.thenReturn(mockResult);

		// when & then
		downloadExcel();
		verify(excelDownloader).downloadAsStream(anyList(), any(HttpServletResponse.class));
	}

	public static void downloadExcel() {
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/admin/point-items/excel")
			.then().log().all()
			.status(HttpStatus.OK);

	}
}
