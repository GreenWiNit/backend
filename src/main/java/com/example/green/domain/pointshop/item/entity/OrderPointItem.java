package com.example.green.domain.pointshop.item.entity;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.pointshop.item.entity.vo.PointItemSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_point_item")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderPointItem extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_item_order_id")
	private Long id;

	@Embedded
	private MemberSnapshot memberSnapshot;

	@Embedded
	private PointItemSnapshot pointItemSnapshot;

}
