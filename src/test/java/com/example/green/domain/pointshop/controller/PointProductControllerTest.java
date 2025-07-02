package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.PointProductResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductUpdateDto;
import com.example.green.domain.pointshop.controller.request.DtoGenerator;
import com.example.green.domain.pointshop.controller.request.PointProductRequest;
import com.example.green.domain.pointshop.service.PointProductService;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.service.command.PointProductUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(PointProductController.class)
class PointProductControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointProductService pointProductService;

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
	void 포인트_상품_수정_요청에_성공한다() {
		// given
		PointProductUpdateDto dto = DtoGenerator.getUpdateDto();

		// when
		NoContent response = PointProductRequest.update(dto);

		// then
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_UPDATE_SUCCESS.getMessage());
		verify(pointProductService).update(any(PointProductUpdateCommand.class), anyLong());
	}
}