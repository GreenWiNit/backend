package com.example.green.domain.pointshop.product.controller;

import static com.example.green.domain.pointshop.product.controller.PointProductResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.green.domain.pointshop.product.controller.dto.PointProductDetail;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.product.service.PointProductQueryService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest({PointProductController.class})
class PointProductControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointProductQueryService pointProductQueryService;
	@MockitoBean
	private PointProductQueryRepository pointProductQueryRepository;

	@Test
	void 커서_기반_포인트_상품_목록_조회에_성공한다() {
		// given
		PointProductView mock = mock(PointProductView.class);
		CursorTemplate<Long, PointProductView> mockResult = CursorTemplate.of(List.of(mock));
		when(pointProductQueryRepository.getProductsByCursor(anyLong())).thenReturn(mockResult);

		// when
		ApiTemplate<CursorTemplate<Long, PointProductView>> response = PointProductRequest.getProducts(1L);

		// then
		assertThat(response.result()).isEqualTo(mockResult);
		assertThat(response.message()).isEqualTo(POINT_PRODUCTS_INQUIRY_SUCCESS.getMessage());
	}

	@Test
	void 포인트_아이디가_주어지면_포인트_상품_상세_조회에_성공한다() {
		// given
		PointProduct mock = getMockPointProductWithStub();
		when(pointProductQueryService.getPointProduct(anyLong())).thenReturn(mock);
		PointProductDetail result = PointProductDetail.forClient(mock);

		// when
		ApiTemplate<PointProductDetail> response = PointProductRequest.getProductById(1L);

		// then
		assertThat(response.result()).isEqualTo(result);
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_DETAIL_INQUIRY_SUCCESS.getMessage());
	}

	public static PointProduct getMockPointProductWithStub() {
		Code code = new Code("PRD-AB-001");
		BasicInfo mockBasicInfo = new BasicInfo("name", "description");
		Media media = new Media("https://image.png");
		Stock mockStock = new Stock(10);
		Price mockPrice = new Price(BigDecimal.ONE);
		PointProduct pointProduct = PointProduct.create(code, mockBasicInfo, media, mockPrice, mockStock);
		ReflectionTestUtils.setField(pointProduct, "id", 1L);
		return pointProduct;
	}
}