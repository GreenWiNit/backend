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

	private InfoImage(
		final InfoEntity info,
		final String imageUrl
	) {
		validateInfoImage(info, imageUrl);
		this.info = info;
		this.imageUrl = imageUrl;
	}

	public static InfoImage create(
		final InfoEntity info,
		final String imageUrl
	) {
		return new InfoImage(info, imageUrl);
	}

	private void validateInfoImage(
		final InfoEntity info,
		final String imageUrl
	) {
		EntityValidator.validateNullData(info, "정보 게시글은 필수입니다.");
		EntityValidator.validateEmptyString(imageUrl, "이미지 URL은 필수입니다.");
	}
}
