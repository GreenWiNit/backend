package com.example.green.domain.pointshop.product.entity;

import static com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.DisplayStatus;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.exception.PointProductException;
import com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage;

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
@Slf4j
public class PointProduct extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_product_id")
	private Long id;
	private Code code;
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

	@Builder
	public PointProduct(Code code, BasicInfo basicInfo, Media media, Price price, Stock stock) {
		validatePointProduct(code, basicInfo, media, price, stock);
		this.code = code;
		this.basicInfo = basicInfo;
		this.media = media;
		this.price = price;
		this.stock = stock;
		this.sellingStatus = SellingStatus.EXCHANGEABLE;
		this.displayStatus = DisplayStatus.DISPLAY;
	}

	public static PointProduct create(Code code, BasicInfo basicInfo, Media media, Price price, Stock stock) {
		return new PointProduct(code, basicInfo, media, price, stock);
	}

	private static void validatePointProduct(Code code, BasicInfo basicInfo, Media media, Price price, Stock stock) {
		validateNullData(code, REQUIRED_CODE);
		validateNullData(basicInfo, REQUIRED_BASIC_INFO);
		validateNullData(media, REQUIRED_MEDIA);
		validateNullData(price, REQUIRED_PRICE);
		validateNullData(stock, REQUIRED_STOCK);
		if (stock.isSoldOut()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_STOCK_CREATION);
		}
	}

	public void updateCode(Code code) {
		validateNullData(code, REQUIRED_CODE);
		this.code = code;
	}

	public void updateBasicInfo(BasicInfo basicInfo) {
		validateNullData(basicInfo, REQUIRED_BASIC_INFO);
		this.basicInfo = basicInfo;
	}

	public void updateMedia(Media media) {
		validateNullData(media, REQUIRED_MEDIA);
		this.media = media;
	}

	public void updatePrice(Price price) {
		validateNullData(price, REQUIRED_PRICE);
		this.price = price;
	}

	public void updateStock(Stock stock) {
		validateNullData(stock, REQUIRED_STOCK);
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

	public boolean isNewImage(Media media) {
		return !this.media.equals(media);
	}

	public String getThumbnailUrl() {
		return this.media.getThumbnailUrl();
	}

	public boolean isDisplay() {
		return this.displayStatus == DisplayStatus.DISPLAY;
	}
}
