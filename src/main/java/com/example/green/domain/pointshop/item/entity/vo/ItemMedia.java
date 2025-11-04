package com.example.green.domain.pointshop.item.entity.vo;

import static com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.net.URI;

import com.example.green.domain.pointshop.item.exception.PointItemException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class ItemMedia {

	@Column(nullable = false, name = "item_thumbnail_url")
	private String itemThumbNailUrl;

	public ItemMedia(String itemThumbNailUrl) {
		validateNullData(itemThumbNailUrl, REQUIRED_ITEM_MEDIA);
		validateItemUri(itemThumbNailUrl);
		this.itemThumbNailUrl = itemThumbNailUrl;
	}

	private static void validateItemUri(String itemThumbNailUrl) {
		try {
			validateAbsoluteItemUri(new URI(itemThumbNailUrl));
		} catch (Exception e) {
			throw new PointItemException(INVALID_ITEM_THUMBNAIL);
		}
	}

	private static void validateAbsoluteItemUri(URI itemUri) {
		if (!itemUri.isAbsolute()) {
			throw new PointItemException(INVALID_ITEM_THUMBNAIL);
		}
	}
}
