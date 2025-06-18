package com.example.green.domain.pointshop.entity;

import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "point_products",
	uniqueConstraints = @UniqueConstraint(name = "uk_point_product_code", columnNames = "code")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;
	private String name;
	private String description;
	private String thumbnail;
	private Integer point;
	private Integer stock;
	private PointProductStatus status;
	private PointProductDisplay display;

	private PointProduct(String code, String name, String description, String thumbnail, Integer point, Integer stock) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.thumbnail = thumbnail;
		this.point = point;
		this.stock = stock;
		this.status = PointProductStatus.IN_STOCK;
		this.display = PointProductDisplay.DISPLAY;
	}

	public static PointProduct create(String code, String name, String description, String thumbnail, Integer point,
		Integer stock) {
		return null;
	}
}