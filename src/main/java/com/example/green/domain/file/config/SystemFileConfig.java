package com.example.green.domain.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class SystemFileConfig {
    
    @Value("${app.profile.default-image-url}")
    private String defaultProfileImageUrl;

    public boolean isSystemFile(String fileUrl) {
        return fileUrl != null && fileUrl.equals(defaultProfileImageUrl);
    }
}