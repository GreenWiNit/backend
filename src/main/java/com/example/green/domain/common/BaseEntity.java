package com.example.green.domain.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime modifiedDate;

	// 생성자 정보 (JWT 인증으로 현재 사용자 자동 설정)
	@CreatedBy
	private String createdBy;

	// 수정자 정보 (JWT 인증으로 현재 사용자 자동 설정)  
	@LastModifiedBy
	private String lastModifiedBy;

	private boolean deleted = false;

	public void markDeleted() {
		this.deleted = true;
	}

	public void restore() {
		this.deleted = false;
	}
}
