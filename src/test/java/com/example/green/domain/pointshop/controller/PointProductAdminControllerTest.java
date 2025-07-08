package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.PointProductResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductUpdateDto;
import com.example.green.domain.pointshop.controller.message.PointProductResponseMessage;
import com.example.green.domain.pointshop.controller.request.DtoGenerator;
import com.example.green.domain.pointshop.controller.request.PointProductRequest;
import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;
import com.example.green.domain.pointshop.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.repository.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.service.PointProductService;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.service.command.PointProductUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.template.base.BaseControllerUnitTest;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(PointProductAdminController.class)
class PointProductAdminControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointProductService pointProductService;
	@MockitoBean
	private PointProductQueryRepository pointProductQueryRepository;
	@MockitoBean
	private ExcelDownloader excelDownloader;

	@Test
	void 포인트_상품_생성_요청에_성공한다() {
		// given
		PointProductCreateDto dto = DtoGenerator.getCreateDto();
		when(pointProductService.create(any(PointProductCreateCommand.class))).thenReturn(1L);

		// when
		ApiTemplate<Long> response = PointProductRequest.create(dto);

		// then
		assertThat(response.result()).isEqualTo(1L);
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_CREATION_SUCCESS.getMessage());
	}

	@Test
	void 포인트_상품_목록_조회에_성공한다() {
		// given
		PointProductSearchResult mock = mock(PointProductSearchResult.class);
		PageTemplate<PointProductSearchResult> mockResult = new PageTemplate<>(0, 0, 0, 0, false, List.of(mock));
		when(pointProductQueryRepository.searchPointProducts(any(PointProductSearchCondition.class)))
			.thenReturn(mockResult);

		// when & then
		ApiTemplate<PageTemplate<PointProductSearchResult>> response = PointProductRequest.searchProducts();
		assertThat(response.result()).usingRecursiveComparison().isEqualTo(mockResult);
		assertThat(response.message()).isEqualTo(
			PointProductResponseMessage.POINT_PRODUCTS_INQUIRY_SUCCESS.getMessage());
	}

	@Test
	void 엑셀_다운로드에_성공한다() {
		// given
		List<PointProductSearchResult> mockResult = List.of(mock(PointProductSearchResult.class));
		when(pointProductQueryRepository.searchPointProductsForExcel(any(PointProductExcelCondition.class)))
			.thenReturn(mockResult);

		// when & then
		PointProductRequest.downloadExcel();
		verify(excelDownloader).downloadAsStream(anyList(), any(HttpServletResponse.class));
	}

	private static PointProductSearchCondition getSearchCondition() {
		return new PointProductSearchCondition(1, 1, "keyword", SellingStatus.SOLD_OUT);
	}

	@Test
	void 포인트_상품_수정_요청에_성공한다() {
		// given
		PointProductUpdateDto dto = DtoGenerator.getUpdateDto();

		// when
		NoContent response = PointProductRequest.update(dto);

		// then
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_UPDATE_SUCCESS.getMessage());
		verify(pointProductService).update(any(PointProductUpdateCommand.class), anyLong());
	}

	@Test
	void 포인트_상품_삭제_요청에_성공한다() {
		// when
		NoContent response = PointProductRequest.delete(1L);

		// then
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_DELETE_SUCCESS.getMessage());
	}

	@Test
	void 포인트_상품_전시_요청에_성공한다() {
		// when
		NoContent response = PointProductRequest.show(1L);
		// then
		assertThat(response.message()).isEqualTo(DISPLAY_SHOW_SUCCESS.getMessage());
	}

	@Test
	void 포인트_상품_미전시_요청에_성공한다() {
		// when
		NoContent response = PointProductRequest.hide(1L);
		// then
		assertThat(response.message()).isEqualTo(DISPLAY_HIDE_SUCCESS.getMessage());
	}
}