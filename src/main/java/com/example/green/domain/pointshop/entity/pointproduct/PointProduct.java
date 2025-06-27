package com.example.green.domain.pointshop.entity.pointproduct;

import org.hibernate.annotations.DynamicUpdate;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.DisplayStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(
	name = "point_products",
	uniqueConstraints = @UniqueConstraint(name = "uk_point_product_code", columnNames = "code")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@DynamicUpdate
@Slf4j
public class PointProduct extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_product_id")
	private Long id;
	@Embedded
	private BasicInfo basicInfo;
	@Embedded
	private Media media;
	@Embedded
	private Price price;
	@Embedded
	private Stock stock;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SellingStatus sellingStatus;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private DisplayStatus displayStatus;

	public static PointProduct create(BasicInfo basicInfo, Media media, Price price, Stock stock) {
		validatePointProduct(basicInfo, media, price, stock);
		return PointProduct.builder()
			.basicInfo(basicInfo)
			.media(media)
			.price(price)
			.stock(stock)
			.sellingStatus(SellingStatus.EXCHANGEABLE)
			.displayStatus(DisplayStatus.DISPLAY)
			.build();
	}

	private static void validatePointProduct(BasicInfo basicInfo, Media media, Price price, Stock stock) {
		validateNullBasicInfo(basicInfo);
		validateNullMedia(media);
		validateNullPrice(price);
		validateNullStock(stock);
		validateStock(stock);
	}

	private static void validateNullBasicInfo(BasicInfo basicInfo) {
		if (basicInfo == null) {
			log.error("상품 기본 정보가 null 값 입니다. 상품 생성 파라미터를 확인해주세요.");
			throw new PointProductException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	private static void validateNullMedia(Media media) {
		if (media == null) {
			log.error("상품 미디어 정보가 null 값 입니다. 상품 생성 파라미터를 확인해주세요.");
			throw new PointProductException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	private static void validateNullPrice(Price price) {
		if (price == null) {
			log.error("상품 가격이 null 값 입니다. 상품 생성 파라미터를 확인해주세요.");
			throw new PointProductException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	private static void validateNullStock(Stock stock) {
		if (stock == null) {
			log.error("상품 재고가 null 값 입니다. 상품 생성 파라미터를 확인해주세요.");
			throw new PointProductException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	private static void validateStock(Stock stock) {
		if (stock.isSoldOut()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_STOCK_CREATION);
		}
	}

	public void updateBasicInfo(BasicInfo basicInfo) {
		validateNullBasicInfo(basicInfo);
		this.basicInfo = basicInfo;
	}

	public void updateMedia(Media media) {
		validateNullMedia(media);
		this.media = media;
	}

	public void updatePrice(Price price) {
		validateNullPrice(price);
		this.price = price;
	}

	public void updateStock(Stock stock) {
		validateNullStock(stock);
		this.stock = stock;
		this.sellingStatus = determineSellingStatus();
	}

	public void decreaseStock(int amount) {
		this.stock = this.stock.decrease(amount);
		this.sellingStatus = determineSellingStatus();
	}

	private SellingStatus determineSellingStatus() {
		if (this.stock.isSoldOut()) {
			return SellingStatus.SOLD_OUT;
		}
		return SellingStatus.EXCHANGEABLE;
	}

	public void showDisplay() {
		this.displayStatus = DisplayStatus.DISPLAY;
	}

	public void hideDisplay() {
		this.displayStatus = DisplayStatus.HIDDEN;
	}
}