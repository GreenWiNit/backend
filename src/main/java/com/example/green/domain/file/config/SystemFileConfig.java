package com.example.green.domain.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * 시스템 파일 설정
 * 기본 이미지 등 시스템에서 관리하는 파일들의 설정을 관리
 */
@Getter
@Configuration
public class SystemFileConfig {
    
    @Value("${app.profile.default-image-url}")
    private String defaultProfileImageUrl;
    
    /**
     * 주어진 URL이 시스템 파일인지 확인
     */
    public boolean isSystemFile(String fileUrl) {
        return fileUrl != null && fileUrl.equals(defaultProfileImageUrl);
    }
}