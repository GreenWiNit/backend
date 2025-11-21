package com.example.green.domain.pointshop.item.entity;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemDisplayStatus;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.entity.vo.ItemStock;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;

import jakarta.persistence.AttributeOverride;
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
	name = "point_item",
	uniqueConstraints = @UniqueConstraint(name = "uk_point_item_code", columnNames = "item_code")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PointItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_item_id")
	private Long id;

	@Embedded
	@AttributeOverride(name = "code", column = @Column(name = "item_code", nullable = false))
	private ItemCode itemCode;

	@Embedded
	private ItemBasicInfo itemBasicInfo;

	@Embedded
	private ItemMedia itemMedia;

	@Embedded
	private ItemPrice itemPrice;

	@Embedded
	private ItemStock itemStock;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SellingStatus sellingStatus;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ItemDisplayStatus displayStatus;

	@Builder
	public PointItem(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia, ItemPrice itemPrice,
		ItemStock itemStock) {
		validatePointItem(itemCode, itemBasicInfo, itemMedia, itemPrice, itemStock);
		this.itemCode = itemCode;
		this.itemBasicInfo = itemBasicInfo;
		this.itemMedia = itemMedia;
		this.itemPrice = itemPrice;
		this.itemStock = itemStock;
		this.sellingStatus = SellingStatus.EXCHANGEABLE;
		this.displayStatus = ItemDisplayStatus.DISPLAY;
	}

	public static PointItem create(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia,
		ItemPrice itemPrice, ItemStock itemStock) {
		return new PointItem(itemCode, itemBasicInfo, itemMedia, itemPrice, itemStock);
	}

	private static void validatePointItem(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia,
		ItemPrice itemPrice, ItemStock itemStock) {
		validateNullData(itemCode, REQUIRED_ITEM_CODE);
		validateNullData(itemBasicInfo, REQUIRED_ITEM_BASIC_INFO);
		validateNullData(itemMedia, REQUIRED_ITEM_MEDIA);
		validateNullData(itemPrice, REQUIRED_ITEM_PRICE);
		validateNullData(itemStock, REQUIRED_ITEM_STOCK);
		if (itemStock.isSoldOut()) {
			throw new PointItemException(INVALID_ITEM_STOCK_CREATION);
		}

	}

	public void updateItemCode(ItemCode itemCode) {
		validateNullData(itemCode, REQUIRED_ITEM_CODE);
		this.itemCode = itemCode;
	}

	public void updateItemBasicInfo(ItemBasicInfo itemBasicInfo) {
		validateNullData(itemBasicInfo, REQUIRED_ITEM_BASIC_INFO);
		this.itemBasicInfo = itemBasicInfo;
	}

	public void updateItemMedia(ItemMedia itemMedia) {
		validateNullData(itemMedia, REQUIRED_ITEM_MEDIA);
		this.itemMedia = itemMedia;
	}

	public void updateItemPrice(ItemPrice itemPrice) {
		validateNullData(itemPrice, REQUIRED_ITEM_PRICE);
		this.itemPrice = itemPrice;
	}

	public void updateItemStock(ItemStock itemStock) {
		validateNullData(itemStock, REQUIRED_ITEM_STOCK);
		this.itemStock = itemStock;
		this.sellingStatus = updateSellingStatus();
	}

	public void decreaseStock(int amount) {
		this.itemStock = this.itemStock.decreaseStock(amount);
		this.sellingStatus = updateSellingStatus();
	}

	private SellingStatus updateSellingStatus() {
		if (this.itemStock.isSoldOut()) {
			return SellingStatus.SOLD_OUT;
		}
		return SellingStatus.EXCHANGEABLE;
	}

	public boolean isNewImage(ItemMedia media) {
		return !this.itemMedia.equals(media);
	}

	public String getThumbnailUrl() {
		return this.itemMedia.getItemThumbNailUrl();
	}

	public void showItemDisplay() {
		this.displayStatus = ItemDisplayStatus.DISPLAY;
	}

	public void hideItemDisplay() {
		this.displayStatus = ItemDisplayStatus.HIDDEN;
	}
}

