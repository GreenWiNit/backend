package com.example.green.domain.challenge;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.service.AdminChallengeService;

/**
 * AdminChallenge 기능 통합 테스트
 * 기존 테스트 오류와 독립적으로 AdminChallenge 기능만 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminChallengeIntegrationTest {

    @Autowired
    private AdminChallengeService adminChallengeService;

    @Test
    void 개인_챌린지를_생성할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        AdminChallengeCreateRequestDto request = new AdminChallengeCreateRequestDto(
            "통합테스트 개인 챌린지",
            1000,
            ChallengeType.PERSONAL,
            now.plusDays(1),
            now.plusDays(7),
            ChallengeDisplayStatus.VISIBLE,
            "https://example.com/integration-test.jpg",
            "통합테스트용 챌린지입니다.",
            null
        );

        // when
        Long challengeId = adminChallengeService.createChallenge(request);

        // then
        assertThat(challengeId).isNotNull();
        assertThat(challengeId).isPositive();
    }

    @Test
    void 팀_챌린지를_생성할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        AdminChallengeCreateRequestDto request = new AdminChallengeCreateRequestDto(
            "통합테스트 팀 챌린지",
            2000,
            ChallengeType.TEAM,
            now.plusDays(1),
            now.plusDays(7),
            ChallengeDisplayStatus.VISIBLE,
            "https://example.com/team-challenge.jpg",
            "통합테스트용 팀 챌린지입니다.",
            10 // maxGroupCount
        );

        // when
        Long challengeId = adminChallengeService.createChallenge(request);

        // then
        assertThat(challengeId).isNotNull();
        assertThat(challengeId).isPositive();
    }

    @Test
    void 챌린지를_생성하고_상세조회할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        AdminChallengeCreateRequestDto createRequest = new AdminChallengeCreateRequestDto(
            "상세조회 테스트 챌린지",
            1500,
            ChallengeType.PERSONAL,
            now.plusDays(1),
            now.plusDays(10),
            ChallengeDisplayStatus.VISIBLE,
            "https://example.com/detail-test.jpg",
            "상세조회 테스트용 챌린지입니다.",
            null
        );

        // when
        Long challengeId = adminChallengeService.createChallenge(createRequest);
        AdminChallengeDetailResponseDto detail = adminChallengeService.getChallengeDetail(challengeId);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.challengeName()).isEqualTo("상세조회 테스트 챌린지");
        assertThat(detail.challengePoint()).isEqualTo(1500);
        assertThat(detail.challengeType()).isEqualTo(ChallengeType.PERSONAL);
        assertThat(detail.displayStatus()).isEqualTo(ChallengeDisplayStatus.VISIBLE);
    }

    @Test
    void 챌린지를_생성하고_수정할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        // 챌린지 생성
        AdminChallengeCreateRequestDto createRequest = new AdminChallengeCreateRequestDto(
            "수정 테스트 챌린지",
            1000,
            ChallengeType.PERSONAL,
            now.plusDays(1),
            now.plusDays(7),
            ChallengeDisplayStatus.VISIBLE,
            "https://example.com/update-test.jpg",
            "수정 전 내용",
            null
        );
        
        Long challengeId = adminChallengeService.createChallenge(createRequest);

        // 수정 요청
        AdminChallengeUpdateRequestDto updateRequest = new AdminChallengeUpdateRequestDto(
            "수정된 챌린지",
            2000,
            now.plusDays(2),
            now.plusDays(14),
            "수정된 내용",
            null
        );

        AdminChallengeDisplayStatusUpdateRequestDto displayStatusUpdateRequest = 
            new AdminChallengeDisplayStatusUpdateRequestDto(ChallengeDisplayStatus.HIDDEN);

        // when
        adminChallengeService.updateChallenge(challengeId, updateRequest);
        adminChallengeService.updateChallengeDisplayStatus(challengeId, displayStatusUpdateRequest);
        AdminChallengeDetailResponseDto detail = adminChallengeService.getChallengeDetail(challengeId);

        // then
        assertThat(detail.challengeName()).isEqualTo("수정된 챌린지");
        assertThat(detail.challengePoint()).isEqualTo(2000);
        assertThat(detail.displayStatus()).isEqualTo(ChallengeDisplayStatus.HIDDEN);
        assertThat(detail.challengeContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 챌린지_이미지를_업데이트할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        AdminChallengeCreateRequestDto createRequest = new AdminChallengeCreateRequestDto(
            "이미지 테스트 챌린지",
            1000,
            ChallengeType.PERSONAL,
            now.plusDays(1),
            now.plusDays(7),
            ChallengeDisplayStatus.VISIBLE,
            "https://example.com/initial-image.jpg",
            "이미지 테스트 내용",
            null
        );
        
        Long challengeId = adminChallengeService.createChallenge(createRequest);
        
        AdminChallengeImageUpdateRequestDto imageRequest = 
            new AdminChallengeImageUpdateRequestDto("https://example.com/new-image.jpg");

        // when
        AdminChallengeDetailResponseDto result = adminChallengeService.updateChallengeImage(challengeId, imageRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.challengeImage()).isEqualTo("https://example.com/new-image.jpg");
    }
} 