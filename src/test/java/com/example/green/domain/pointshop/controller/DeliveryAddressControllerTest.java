package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.DeliveryAddressResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.controller.request.DeliveryAddressRequest;
import com.example.green.domain.pointshop.service.DeliveryAddressService;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(DeliveryAddressController.class)
class DeliveryAddressControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private DeliveryAddressService deliveryAddressService;

	@Test
	void 배송지_정보_저장_요청에_성공한다() {
		// given
		DeliveryAddressCreateDto createDto = getCreateDto();
		when(deliveryAddressService.saveForSingleAddress(any(DeliveryAddressCreateCommand.class))).thenReturn(1L);

		// when
		ApiTemplate<Long> response = DeliveryAddressRequest.create(createDto);

		// then
		assertThat(response.message()).isEqualTo(DELIVERY_ADDRESS_ADD_SUCCESS.getMessage());
		assertThat(response.result()).isEqualTo(1L);
	}

	private static DeliveryAddressCreateDto getCreateDto() {
		return new DeliveryAddressCreateDto(
			"홍길동",
			"010-1234-5678",
			"부산광역시 남구 유엔평화로 29번길 54",
			"307호",
			"48503"
		);
	}
}