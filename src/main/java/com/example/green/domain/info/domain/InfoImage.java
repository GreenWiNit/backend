package com.example.green.domain.info.domain;

import org.hibernate.annotations.Where;

import com.example.green.domain.common.BaseEntity;
import com.example.green.global.utils.EntityValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "INFO_IMAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
public class InfoImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "info_image_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "info_id", nullable = false)
	@JsonIgnore
	private InfoEntity info;

	@Column(nullable = false, length = 500)
	private String imageUrl;

	@Column(nullable = false)
	private Integer displayOrder;

	private InfoImage(
		final InfoEntity info,
		final String imageUrl,
		final Integer displayOrder
	) {
		validateInfoImage(info, imageUrl, displayOrder);
		this.info = info;
		this.imageUrl = imageUrl;
		this.displayOrder = displayOrder;
	}

	public static InfoImage create(
		final InfoEntity info,
		final String imageUrl,
		final Integer displayOrder
	) {
		return new InfoImage(info, imageUrl, displayOrder);
	}

	private void validateInfoImage(
		final InfoEntity info,
		final String imageUrl,
		final Integer displayOrder
	) {
		EntityValidator.validateNullData(info, "정보 게시글은 필수입니다.");
		EntityValidator.validateEmptyString(imageUrl, "이미지 URL은 필수입니다.");
		EntityValidator.validateNullData(displayOrder, "이미지 순서는 필수입니다.");

		if (displayOrder < 0) {
			throw new IllegalArgumentException("이미지 순서는 0 이상이어야 합니다.");
		}
	}
}
