package com.example.green.global.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class UlidUtilsTest {

    @Test
    @DisplayName("ULID 생성 기본 동작 테스트")
    void shouldGenerateValidUlid() {
        // when
        String ulid = UlidUtils.generate();
        
        // then
        assertNotNull(ulid);
        assertEquals(26, ulid.length());
        assertTrue(UlidUtils.isValid(ulid));
    }

    @Test
    @DisplayName("동일한 시점에 생성된 ULID들은 서로 다르다")
    void shouldGenerateDifferentUlids() {
        // when
        String ulid1 = UlidUtils.generate();
        String ulid2 = UlidUtils.generate();
        
        // then
        assertNotEquals(ulid1, ulid2);
        assertTrue(UlidUtils.isValid(ulid1));
        assertTrue(UlidUtils.isValid(ulid2));
    }

    @Test
    @DisplayName("ULID에서 타임스탬프 추출 테스트")
    void shouldExtractTimestampFromUlid() {
        // given
        long timestamp = System.currentTimeMillis();
        
        // when
        String ulid = UlidUtils.generate(timestamp);
        long extractedTimestamp = UlidUtils.extractTimestamp(ulid);
        
        // then
        assertEquals(timestamp, extractedTimestamp);
    }

    @Test
    @DisplayName("유효하지 않은 ULID 형식 검증")
    void shouldValidateInvalidUlidFormat() {
        // given
        String invalidUlid1 = "123"; // 너무 짧음
        String invalidUlid2 = "01ARZ3NDEKTSV4RRFFQ69G5FAVTOOLONG"; // 너무 김
        String invalidUlid3 = "01ARZ3NDEKTSV4RRFFQ69G5FA!"; // 유효하지 않은 문자
        
        // when & then
        assertFalse(UlidUtils.isValid(invalidUlid1));
        assertFalse(UlidUtils.isValid(invalidUlid2));
        assertFalse(UlidUtils.isValid(invalidUlid3));
        assertFalse(UlidUtils.isValid(null));
    }

    @Test
    @DisplayName("유효한 ULID 형식 검증")
    void shouldValidateValidUlidFormat() {
        // given
        String validUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
        
        // when & then
        assertTrue(UlidUtils.isValid(validUlid));
    }

    @Test
    @DisplayName("연속으로 생성된 ULID들은 시간순으로 정렬 가능하다")
    void shouldGenerateOrderedUlids() throws InterruptedException {
        // given
        String ulid1 = UlidUtils.generate();
        Thread.sleep(1); // 1ms 간격
        String ulid2 = UlidUtils.generate();
        
        // when
        long timestamp1 = UlidUtils.extractTimestamp(ulid1);
        long timestamp2 = UlidUtils.extractTimestamp(ulid2);
        
        // then
        assertTrue(timestamp1 <= timestamp2);
        assertTrue(ulid1.compareTo(ulid2) <= 0); // 문자열 비교에서도 순서가 맞음
    }
} 