package com.example.green.domain.pointshop.entity;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.vo.Media;
import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;
import com.example.green.domain.pointshop.entity.vo.Price;
import com.example.green.domain.pointshop.entity.vo.Stock;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "point_products",
	uniqueConstraints = @UniqueConstraint(name = "uk_point_product_code", columnNames = "code")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PointProduct extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Embedded
	private BasicInfo basicInfo;
	@Embedded
	private Media media;
	@Embedded
	private Price price;
	@Embedded
	private Stock stock;
	private PointProductStatus status;
	private PointProductDisplay display;

	public static PointProduct create(BasicInfo basicInfo, Media media, Price price, Stock stock) {
		return PointProduct.builder()
			.basicInfo(basicInfo)
			.media(media)
			.price(price)
			.stock(stock)
			.status(PointProductStatus.IN_STOCK)
			.display(PointProductDisplay.DISPLAY)
			.build();
	}

	public void updateBasicInfo(String code, String name, String description) {
		this.basicInfo = new BasicInfo(code, name, description);
	}

	public void updateMedia(String thumbnailUrl) {
		this.media = new Media(thumbnailUrl);
	}

	public void updatePrice(Integer price) {
		this.price = new Price(price);
	}

	public void updateStock(Integer stock) {
		// todo: 관리자가 update 시 수량이 0개가 아니라면 매진 상태를 자동으로 해제 할 것인지? 물어보기
		this.stock = new Stock(stock);
		if (this.stock.isOutOfStock()) {
			this.status = PointProductStatus.OUT_OF_STOCK;
		}
	}

	public void decreaseStock(int amount) {
		this.stock = stock.decrease(amount);
		if (stock.isOutOfStock()) {
			this.status = PointProductStatus.OUT_OF_STOCK;
		}
	}

	public void markAsSoldOut() {
		// todo: 관리자가 매진 처리시 수량 0개로 되도록 하는지 물어보기
		this.status = PointProductStatus.OUT_OF_STOCK;
	}

	public void backInStock() {
		this.status = PointProductStatus.IN_STOCK;
	}

	public void show() {
		this.display = PointProductDisplay.DISPLAY;
	}

	public void hide() {
		this.display = PointProductDisplay.HIDDEN;
	}
}