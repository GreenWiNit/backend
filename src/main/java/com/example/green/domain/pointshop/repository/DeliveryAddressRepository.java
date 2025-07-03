package com.example.green.domain.pointshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

	boolean existsByRecipientId(Long recipientId);
}
