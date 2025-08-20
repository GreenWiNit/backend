package com.example.green.domain.file.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.file.domain.FileEntity;
import com.example.green.domain.file.domain.vo.FileMetaData;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.repository.FileJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시스템 파일 초기화
 * 애플리케이션 시작 시 시스템 파일(기본 프로필 이미지 등)을 DB에 등록
 */
@Slf4j
@Component
@Order(1) // 가장 먼저 실행
@RequiredArgsConstructor
public class SystemFileInitializer implements ApplicationRunner {

    private final FileJpaRepository fileJpaRepository;
    private final SystemFileConfig systemFileConfig;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeDefaultProfileImage();
    }

    private void initializeDefaultProfileImage() {
        String defaultImageUrl = systemFileConfig.getDefaultProfileImageUrl();
        
        // 이미 존재하는지 확인 (전체 URL로 저장됨)
        if (fileJpaRepository.findByFileKey(defaultImageUrl).isPresent()) {
            log.info("기본 프로필 이미지가 이미 등록되어 있습니다: {}", defaultImageUrl);
            return;
        }
        
        // 시스템 파일로 등록
        FileMetaData metaData = FileMetaData.createForSystemFile(
            "default-profile.png",
            "image/png",
            0L // 시스템 파일은 크기 정보 불필요
        );
        
        FileEntity systemFile = FileEntity.createSystemFile(
            metaData, 
            defaultImageUrl, // 전체 URL을 파일 키로 사용
            Purpose.PROFILE
        );
        
        fileJpaRepository.save(systemFile);
        log.info("기본 프로필 이미지를 시스템 파일로 등록했습니다: {}", defaultImageUrl);
    }
}