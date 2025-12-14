package com.example.green.domain.pointshop.item.entity;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.item.entity.vo.Category;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemDisplayStatus;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;

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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ItemDisplayStatus displayStatus;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Builder
	public PointItem(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia, ItemPrice itemPrice,
		Category category) {
		validatePointItem(itemCode, itemBasicInfo, itemMedia, itemPrice);
		this.itemCode = itemCode;
		this.itemBasicInfo = itemBasicInfo;
		this.itemMedia = itemMedia;
		this.itemPrice = itemPrice;
		this.displayStatus = ItemDisplayStatus.DISPLAY;
		this.category = category;
	}

	public static PointItem create(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia,
		ItemPrice itemPrice, Category category) {
		return new PointItem(itemCode, itemBasicInfo, itemMedia, itemPrice, category);
	}

	private static void validatePointItem(ItemCode itemCode, ItemBasicInfo itemBasicInfo, ItemMedia itemMedia,
		ItemPrice itemPrice) {
		validateNullData(itemCode, REQUIRED_ITEM_CODE);
		validateNullData(itemBasicInfo, REQUIRED_ITEM_BASIC_INFO);
		validateNullData(itemMedia, REQUIRED_ITEM_MEDIA);
		validateNullData(itemPrice, REQUIRED_ITEM_PRICE);
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

