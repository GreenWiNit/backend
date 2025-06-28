package com.example.green.domain.info.domain;

import org.hibernate.annotations.GenericGenerator;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.info.domain.vo.InfoCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "INFO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InfoEntity extends BaseEntity {
	@Id
	@GeneratedValue(generator = "info-id-gen")
	@GenericGenerator(
		name = "info-id-gen",
		strategy = "com.example.green.domain.info.utils.PrefixSequenceIdGenerator"
	)
	@Column(name = "info_id", length = 7, nullable = false, updatable = false)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR(20)")
	private InfoCategory infoCategory;

	@Column(length = 30, nullable = false)
	private String title;

	@Column(length = 1000, nullable = false)
	private String content;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false, columnDefinition = "CHAR(1)")
	private String isDisplay;

	@Builder
	private InfoEntity(
		final String title,
		final String content,
		final InfoCategory infoCategory,
		final String isDisplay,
		final String imageUrl
	) {
		this.title = title;
		this.content = content;
		this.infoCategory = infoCategory;
		this.imageUrl = imageUrl;
		this.isDisplay = isDisplay;
	}

	// 변수 직접 받아 필드 변경 -> 도미엔단 외부 영향도 최소화
	public void update(
		final String updateTitle,
		final String updateContent,
		final InfoCategory updateInfoCategory,
		final String updateImageUrl,
		final String updateIsDisplay
	) {
		this.title = updateTitle;
		this.content = updateContent;
		this.infoCategory = updateInfoCategory;
		this.imageUrl = updateImageUrl;
		this.isDisplay = updateIsDisplay;
	}
}
