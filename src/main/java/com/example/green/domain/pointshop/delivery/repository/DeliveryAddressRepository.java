package com.example.green.domain.pointshop.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.delivery.entity.DeliveryAddress;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

	boolean existsByRecipientId(Long recipientId);

	Optional<DeliveryAddress> findByRecipientId(Long recipientId);

	boolean existsByIdAndRecipientId(Long deliveryAddressId, Long recipientId);
}
