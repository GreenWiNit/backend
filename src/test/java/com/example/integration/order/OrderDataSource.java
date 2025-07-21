package com.example.integration.order;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.integration.common.BaseDataSource;

@TestComponent
public class OrderDataSource extends BaseDataSource {

	public OrderDataSource(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	public void createOrder() {
		jdbcTemplate.execute("""
			    CREATE TABLE ORDERS (
			        TOTAL_PRICE decimal(38,2) not null, 
			        CREATED_DATE datetime(6) not null, 
			        DELIVERY_ADDRESS_ID bigint not null, 
			        MEMBER_ID bigint not null, 
			        MODIFIED_DATE datetime(6), 
			        ORDER_ID bigint not null auto_increment, 
			        DETAIL_ADDRESS varchar(255) not null, 
			        MEMBER_CODE varchar(255) not null, 
			        ORDER_NUMBER varchar(255), 
			        PHONE_NUMBER varchar(255) not null, 
			        RECIPIENT_NAME varchar(255) not null, 
			        ROAD_ADDRESS varchar(255) not null, 
			        ZIP_CODE varchar(255) not null, 
			        STATUS enum ('CANCELLED','DELIVERED','PENDING_DELIVERY','SHIPPING') not null, 
			        primary key (ORDER_ID)
			    ) engine=InnoDB
			""");
	}

	public void createOrderItems() {
		jdbcTemplate.execute("""
			    CREATE TABLE ORDER_ITEMS (
			        QUANTITY integer not null, 
			        UNIT_PRICE decimal(38,2) not null, 
			        CREATED_DATE datetime(6) not null, 
			        MODIFIED_DATE datetime(6), 
			        ORDER_ID bigint not null, 
			        ORDER_ITEM_ID bigint not null auto_increment, 
			        POINT_PRODUCT_ID bigint not null, 
			        ITEM_CODE varchar(255) not null, 
			        ITEM_NAME varchar(255) not null, 
			        primary key (ORDER_ITEM_ID),
			        FOREIGN KEY (ORDER_ID) REFERENCES ORDERS(ORDER_ID)
			    ) engine=InnoDB
			""");
	}

	public void deleteOrder() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS ORDERS");
	}

	public void deleteOrderItems() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS ORDER_ITEMS");
	}
}
