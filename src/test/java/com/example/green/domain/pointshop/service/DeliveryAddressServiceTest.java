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
import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
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
	void 배송지_정보가_없고_휴대전화_인증을_했을_경우_단일_배송지_정보가_저장된다() {
		// given
		when(deliveryAddressRepository.existsByRecipientId(anyLong())).thenReturn(false);
		DeliveryAddressCreateCommand command = getCommand();
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
		DeliveryResult result = deliveryAddressService.getDeliveryAddressByRecipient(1L);

		// then
		assertThat(result).isEqualTo(deliveryResult);
	}

	@Test
	void 기존_배송지_정보가_없다면_예외가_발생한다() {
		// given
		when(deliveryAddressRepository.findByRecipientId(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryAddressService.getDeliveryAddressByRecipient(1L))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", NOT_FOUND_DELIVERY_ADDRESS);
	}

	@Test
	void 배송지_주소_식별자로_배송지_스냅샷을_가져올_수_있다() {
		// given
		DeliveryAddress mock = mock(DeliveryAddress.class);
		Recipient recipient = Recipient.of("이름", "010-1234-5678");
		Address address = Address.of("도로명", "상세주소", "12345");
		when(mock.getRecipient()).thenReturn(recipient);
		when(mock.getAddress()).thenReturn(address);
		when(mock.getId()).thenReturn(1L);
		when(deliveryAddressRepository.findById(anyLong())).thenReturn(Optional.of(mock));

		// when
		DeliveryAddressSnapshot snapshot = deliveryAddressService.getSnapshot(1L);

		// then
		assertThat(snapshot.getDeliveryAddressId()).isEqualTo(1L);
		assertThat(snapshot.getRecipientName()).isEqualTo(recipient.getRecipientName());
		assertThat(snapshot.getPhoneNumber()).isEqualTo(recipient.getPhoneNumber());
		assertThat(snapshot.getRoadAddress()).isEqualTo(address.getRoadAddress());
		assertThat(snapshot.getDetailAddress()).isEqualTo(address.getDetailAddress());
		assertThat(snapshot.getZipCode()).isEqualTo(address.getZipCode());
	}

	@Test
	void 배송지_주소_식별자가_존재하지_않으면_예외가_발생한다() {
		// given
		when(deliveryAddressRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryAddressService.getSnapshot(1L))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", NOT_FOUND_DELIVERY_ADDRESS);
	}

	@Test
	void 배송지_주소_식별자와_수령자_식별자로_구성된_소유자_정보가_없으면_예외가_발생한다() {
		// given
		when(deliveryAddressRepository.existsByIdAndRecipientId(anyLong(), anyLong())).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> deliveryAddressService.validateAddressOwnership(1L, 1L))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_OWNERSHIP);
	}

	@Test
	void 배송지_주소_식별자와_수령자_식별자로_구성된_소유자_정보가_있으면_검증에_성공한다() {
		// given
		when(deliveryAddressRepository.existsByIdAndRecipientId(anyLong(), anyLong())).thenReturn(true);

		// when & then
		assertThatCode(() -> deliveryAddressService.validateAddressOwnership(1L, 1L))
			.doesNotThrowAnyException();
	}

	private static DeliveryAddressCreateCommand getCommand() {
		return new DeliveryAddressCreateCommand(
			1L,
			Recipient.of("이름", "010-1234-5678"),
			Address.of("도로명", "상세", "12345")
		);
	}
}