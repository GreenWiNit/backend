package com.example.green.domain.pointshop.entity;

import java.net.URI;
import java.util.regex.Pattern;

import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;

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

	private static final String POINT_CODE_REGEX = "^PRD-[A-Z]{2}-\\d{3}$";
	private static final Pattern POINT_CODE_PATTERN = Pattern.compile(POINT_CODE_REGEX);
	private static final int POINT_NAME_MIN_LENGTH = 2;
	private static final int POINT_NAME_MAX_LENGTH = 15;
	private static final int POINT_DESCRIPTION_MAX_LENGTH = 100;

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

	private PointProduct(String code, String name, String description, String thumbnailUrl, Integer price,
		Integer stock) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.price = price;
		this.stock = stock;
		this.status = PointProductStatus.IN_STOCK;
		this.display = PointProductDisplay.DISPLAY;
	}

	public static PointProduct create(String code, String name, String description,
		String thumbnail, Integer point, Integer stock) {
		if (code == null || !POINT_CODE_PATTERN.matcher(code.trim()).matches()) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_CODE);
		}
		if (point == null || point < 0) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_POINT);
		}
		if (stock == null || stock < 1) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_STOCK);
		}
		if (name == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_NAME);
		}
		final String trimmedName = name.trim();
		if (trimmedName.length() < POINT_NAME_MIN_LENGTH || trimmedName.length() > POINT_NAME_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_NAME);
		}
		if (thumbnail == null) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
		try {
			URI uri = new URI(thumbnail.trim());
			if (!uri.isAbsolute()) {
				throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
			}
		} catch (Exception e) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_THUMBNAIL);
		}
		if (description == null || description.trim().length() > POINT_DESCRIPTION_MAX_LENGTH) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_PRODUCT_DESCRIPTION);
		}
		return new PointProduct(code, trimmedName, description, thumbnail, point, stock);
	}
}