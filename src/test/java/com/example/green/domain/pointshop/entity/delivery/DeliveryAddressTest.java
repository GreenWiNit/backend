package com.example.green.domain.pointshop.entity.delivery;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;

class DeliveryAddressTest {

	@Test
	void 배송지_주소를_생성한다() {
		// given
		Address address = Address.of("부산광역시 남구 유엔평화로 29번길 54", "307호", "48503");
		Recipient recipient = Recipient.of("김지환", "010-7553-6092");

		// when
		DeliveryAddress deliveryAddress = DeliveryAddress.create(1L, recipient, address);

		// then
		assertThat(deliveryAddress.getRecipient()).isEqualTo(recipient);
		assertThat(deliveryAddress.getAddress()).isEqualTo(address);
	}

}