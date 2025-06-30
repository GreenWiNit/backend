package com.example.green.domain.info.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.utils.EntityValidator;

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
@Where(clause = "deleted = false AND is_display = 'Y'")
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

	/**
	 * 전시 여부
	 * - Y: 전시
	 * - N: 비전시
	 */
	@Column(nullable = false, columnDefinition = "CHAR(1)")
	private String isDisplay;

	@Builder
	private InfoEntity(
		final String title,
		final String content,
		final InfoCategory infoCategory,
		final String imageUrl,
		final String isDisplay
	) {
		validateNullInfo(title, content, infoCategory, isDisplay);
		this.title = title;
		this.content = content;
		this.infoCategory = infoCategory;
		this.imageUrl = imageUrl;
		this.isDisplay = validateFormatIsDisplay(isDisplay);
	}

	// 변수 직접 받아 필드 변경 -> 도미엔단 외부 영향도 최소화
	public void update(
		final String updateTitle,
		final String updateContent,
		final InfoCategory updateInfoCategory,
		final String updateImageUrl,
		final String updateIsDisplay
	) {
		validateNullInfo(updateTitle, updateContent, updateInfoCategory, updateIsDisplay);
		this.title = updateTitle;
		this.content = updateContent;
		this.infoCategory = updateInfoCategory;
		this.imageUrl = updateImageUrl;
		this.isDisplay = validateFormatIsDisplay(updateIsDisplay);
	}

	private void validateNullInfo(
		String title,
		String content,
		InfoCategory infoCategory,
		String isDisplay
	) {
		EntityValidator.validateEmptyString(title, "제목은 필수입니다.");
		EntityValidator.validateEmptyString(content, "내용은 필수입니다.");
		EntityValidator.validateNullData(infoCategory, "카테고리는 필수입니다.");
		EntityValidator.validateEmptyString(isDisplay, "전시 여부는 필수입니다.");
	}

	private String validateFormatIsDisplay(String isDisplay) {
		if ("y".equalsIgnoreCase(isDisplay)) {
			isDisplay = "Y";
		} else if ("n".equalsIgnoreCase(isDisplay)) {
			isDisplay = "N";
		} else {
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
		return isDisplay;
	}
}
