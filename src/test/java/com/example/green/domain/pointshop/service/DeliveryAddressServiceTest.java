package com.example.green.domain.pointshop.service;

import static com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;
import com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressException;
import com.example.green.domain.pointshop.repository.DeliveryAddressRepository;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;
import com.example.green.domain.pointshop.service.result.DeliveryResult;

@ExtendWith(MockitoExtension.class)
class DeliveryAddressServiceTest {

	@Mock
	private DeliveryAddressRepository deliveryAddressRepository;

	@InjectMocks
	private DeliveryAddressService deliveryAddressService;

	@Test
	void 배송지_정보가_없다면_단일_배송지_정보가_저장된다() {
		// given
		when(deliveryAddressRepository.existsByRecipientId(anyLong())).thenReturn(false);
		DeliveryAddressCreateCommand command = new DeliveryAddressCreateCommand(
			1L,
			Recipient.of("이름", "010-1234-5678"),
			Address.of("도로명", "상세", "12345")
		);
		DeliveryAddress mockDeliveryAddress = mock(DeliveryAddress.class);
		when(deliveryAddressRepository.save(any(DeliveryAddress.class))).thenReturn(mockDeliveryAddress);
		when(mockDeliveryAddress.getId()).thenReturn(1L);

		// when
		Long result = deliveryAddressService.saveSingleAddress(command);

		// then
		assertThat(result).isEqualTo(1L);
	}

	@Test
	void 배송지_정보가_있다면_예외가_발생한다() {
		// given
		DeliveryAddressCreateCommand mockCommand = mock(DeliveryAddressCreateCommand.class);
		when(deliveryAddressRepository.existsByRecipientId(anyLong())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> deliveryAddressService.saveSingleAddress(mockCommand))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", DELIVERY_ADDRESS_ALREADY_EXISTS);
	}

	@Test
	void 기존_배송지_정보가_있다면_배송지_조회에_성공한다() {
		// given
		Recipient recipient = Recipient.of("이름", "010-1234-5678");
		Address address = Address.of("도로명", "상세주소", "12345");
		DeliveryAddress mockDeliveryAddress = DeliveryAddress.create(1L, recipient, address);
		when(deliveryAddressRepository.findByRecipientId(anyLong())).thenReturn(Optional.of(mockDeliveryAddress));
		DeliveryResult deliveryResult = DeliveryResult.of(1L, recipient, address);

		// when
		DeliveryResult result = deliveryAddressService.getDeliveryAddress(1L);

		// then
		assertThat(result).isEqualTo(deliveryResult);
	}

	@Test
	void 기존_배송지_정보가_없다면_예외가_발생한다() {
		// given
		when(deliveryAddressRepository.findByRecipientId(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryAddressService.getDeliveryAddress(1L))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", NOT_FOUND_DELIVERY_ADDRESS);
	}
}