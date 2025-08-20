package com.example.green.domain.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class ProfileConfig {
    
    @Value("${app.profile.default-image-url}")
    private String defaultProfileImageUrl;
}