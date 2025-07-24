package com.example.integration.order;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.green.domain.pointshop.delivery.service.DeliveryAddressService;
import com.example.green.domain.pointshop.order.client.PointSpendClient;
import com.example.green.domain.pointshop.order.client.dto.PointSpendRequest;
import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.product.service.PointProductService;

@TestConfiguration
public class OrderTestConfig {

	@Bean
	@Primary
	public PointProductService pointProductService() {
		PointProductService pointProductService = mock(PointProductService.class);
		ItemSnapshot mockItem = new ItemSnapshot(1L, "아이템", "가짜 코드", BigDecimal.valueOf(1000));
		when(pointProductService.getItemSnapshot(anyLong())).thenReturn(mockItem);
		doNothing().when(pointProductService).decreaseSingleItemStock(anyLong(), anyInt());
		return pointProductService;
	}

	@Bean
	@Primary
	public DeliveryAddressService deliveryAddressService() {
		DeliveryAddressService deliveryAddressService = mock(DeliveryAddressService.class);
		when(deliveryAddressService.getSnapshot(anyLong()))
			.thenReturn(DeliveryAddressSnapshot.of(1L, "김지환", "010-75536-6092", "도로명", "상세", "12345"));
		doNothing().when(deliveryAddressService).validateAddressOwnership(anyLong(), anyLong());
		return deliveryAddressService;
	}

	@Bean
	@Primary
	public PointSpendClient pointSpendClient() {
		PointSpendClient pointSpendClient = mock(PointSpendClient.class);
		doNothing().when(pointSpendClient).spendPoints(any(PointSpendRequest.class));
		return pointSpendClient;
	}
}