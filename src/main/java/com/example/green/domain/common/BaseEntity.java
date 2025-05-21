package com.example.green.domain.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 생성자 정보 (현재는 Security 설정이 되어있지 않아 주석처리)
    // @CreatedBy
    private String createdBy;

    // 수정자 정보 (현재는 Security 설정이 되어있지 않아 주석처리)
    // @LastModifiedBy
    private String lastModifiedBy;

    private boolean isDeleted = false;
}
