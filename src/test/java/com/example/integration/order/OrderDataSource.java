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
			    CREATE TABLE orders (
			        total_price decimal(38,2) not null, 
			        created_date timestamp not null, 
			        delivery_address_id bigint not null, 
			        member_id bigint not null, 
			        modified_date timestamp, 
			        order_id bigserial primary key, 
			        detail_address varchar(255) not null, 
			        member_key varchar(255) not null, 
			        member_email varchar(255) not null, 
			        order_number varchar(255), 
			        phone_number varchar(255) not null, 
			        recipient_name varchar(255) not null, 
			        road_address varchar(255) not null, 
			        zip_code varchar(255) not null, 
			        status varchar(20) not null CHECK (status IN ('CANCELLED','DELIVERED','PENDING_DELIVERY','SHIPPING'))
			    )
			""");
	}

	public void createOrderItems() {
		jdbcTemplate.execute("""
			    CREATE TABLE order_items (
			        quantity integer not null, 
			        unit_price decimal(38,2) not null, 
			        created_date timestamp not null, 
			        modified_date timestamp, 
			        order_id bigint not null, 
			        order_item_id bigserial primary key, 
			        point_product_id bigint not null, 
			        item_code varchar(255) not null, 
			        item_name varchar(255) not null, 
			        FOREIGN KEY (order_id) REFERENCES orders(order_id)
			    )
			""");
	}

	public void deleteOrder() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS orders CASCADE");
	}

	public void deleteOrderItems() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS order_items");
	}
}