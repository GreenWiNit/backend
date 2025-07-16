package com.example.green.domain.pointshop.entity.delivery;

import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DeliveryAddress extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_id")
	private Long id;
	@Column(nullable = false)
	private Long recipientId;
	private Recipient recipient;
	private Address address;

	private DeliveryAddress(Long recipientId, Recipient recipient, Address address) {
		validateAutoIncrementId(recipientId, "물품 수령자 ID는 필수 값 입니다.");
		validateNullData(recipient, "물품 수령자 정보는 필수 값 입니다.");
		validateNullData(address, "물품 수령 주소 정보는 필수 값 입니다.");
		this.recipientId = recipientId;
		this.recipient = recipient;
		this.address = address;
	}

	public static DeliveryAddress create(Long recipientId, Recipient recipient, Address address) {
		return new DeliveryAddress(recipientId, recipient, address);
	}

	public void updateRecipient(Recipient recipient) {
		validateNullData(recipient, "물품 수령자 정보는 필수 값 입니다.");
		this.recipient = recipient;
	}

	public void updateAddress(Address address) {
		validateNullData(address, "물품 수령 주소 정보는 필수 값 입니다.");
		this.address = address;
	}
}
