package com.example.green.domain.pointshop.entity.pointproduct.vo;

import java.net.URI;

import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

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
public class Media {

	@Column(nullable = false)
	private String thumbnailUrl;

	public Media(String thumbnailUrl) {
		validateNullCheck(thumbnailUrl);
		validateUri(thumbnailUrl);
		this.thumbnailUrl = thumbnailUrl;
	}

	private static void validateNullCheck(String thumbnailUrl) {
		if (thumbnailUrl == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
	}

	private static void validateUri(String thumbnailUrl) {
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