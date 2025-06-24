package com.example.green.domain.pointshop.entity.vo;

import java.net.URI;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

public record Media(String thumbnailUrl) {

	public Media {
		if (thumbnailUrl == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
		thumbnailUrl = thumbnailUrl.trim();
		try {
			validateAbsoluteUri(new URI(thumbnailUrl));
		} catch (Exception e) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
	}

	private static void validateAbsoluteUri(URI uri) {
		if (!uri.isAbsolute()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
	}
}