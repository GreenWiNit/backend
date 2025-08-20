package com.example.green.domain.file.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.green.domain.file.domain.vo.FileMetaData;
import com.example.green.domain.file.domain.vo.FileStatus;
import com.example.green.domain.file.domain.vo.Purpose;

@DisplayName("시스템 파일 테스트")
class SystemFileTest {

    @Test
    @DisplayName("시스템 파일 생성 시 항상 PERMANENT 상태")
    void createSystemFile_AlwaysPermanent() {
        // given
        FileMetaData metaData = FileMetaData.createForSystemFile(
            "default-profile.png",
            "image/png",
            1024L
        );
        
        // when
        FileEntity systemFile = FileEntity.createSystemFile(
            metaData,
            "images/profile/default.png",
            Purpose.PROFILE
        );
        
        // then
        assertThat(systemFile.isSystemFile()).isTrue();
        assertThat(systemFile.getFileStatus()).isEqualTo(FileStatus.PERMANENT);
    }
    
    @Test
    @DisplayName("시스템 파일은 삭제 시도해도 삭제되지 않음")
    void systemFile_CannotBeDeleted() {
        // given
        FileEntity systemFile = FileEntity.createSystemFile(
            FileMetaData.createForSystemFile("default.png", "image/png", 0L),
            "images/profile/default.png",
            Purpose.PROFILE
        );
        
        // when
        systemFile.markDeleted(); // 시스템 파일은 삭제되지 않음
        
        // then
        assertThat(systemFile.isDeleted()).isFalse();
        assertThat(systemFile.getFileStatus()).isEqualTo(FileStatus.PERMANENT);
    }
    
    @Test
    @DisplayName("일반 파일은 정상적으로 삭제 가능")
    void normalFile_CanBeDeleted() {
        // given
        FileEntity normalFile = FileEntity.create(
            FileMetaData.createForSystemFile("user.png", "image/png", 1024L),
            "images/profile/user.png",
            Purpose.PROFILE
        );
        normalFile.markAsPermanent();
        
        // when
        normalFile.markDeleted();
        
        // then
        assertThat(normalFile.isDeleted()).isTrue();
    }
    
    @Test
    @DisplayName("시스템 파일 markAsPermanent 호출해도 상태 유지")
    void systemFile_MarkAsPermanent_NoChange() {
        // given
        FileEntity systemFile = FileEntity.createSystemFile(
            FileMetaData.createForSystemFile("default.png", "image/png", 0L),
            "images/profile/default.png",
            Purpose.PROFILE
        );
        
        // when
        systemFile.markAsPermanent(); // 이미 PERMANENT이므로 변화 없음
        
        // then
        assertThat(systemFile.getFileStatus()).isEqualTo(FileStatus.PERMANENT);
    }
}