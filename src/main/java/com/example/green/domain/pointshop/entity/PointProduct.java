package com.example.green.domain.pointshop.entity;

import com.example.green.domain.pointshop.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.vo.Media;
import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;
import com.example.green.domain.pointshop.entity.vo.Price;
import com.example.green.domain.pointshop.entity.vo.Stock;

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
	private String thumbnailUrl;
	private Integer price;
	private Integer stock;
	private PointProductStatus status;
	private PointProductDisplay display;

	private PointProduct(BasicInfo basicInfo, Media media, Price price, Stock stock) {
		this.code = basicInfo.code();
		this.name = basicInfo.name();
		this.description = basicInfo.description();
		this.thumbnailUrl = media.thumbnailUrl();
		this.price = price.price();
		this.stock = stock.stock();
		this.status = PointProductStatus.IN_STOCK;
		this.display = PointProductDisplay.DISPLAY;
	}

	public static PointProduct create(String code, String name, String description,
		String thumbnailUrl, Integer price, Integer stock) {
		return new PointProduct(
			new BasicInfo(code, name, description),
			new Media(thumbnailUrl),
			new Price(price),
			new Stock(stock)
		);
	}
}