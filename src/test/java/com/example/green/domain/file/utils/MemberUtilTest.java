package com.example.green.domain.file.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.green.domain.member.entity.Member;
import com.example.green.global.utils.MemberUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberUtilTest {

    @Autowired
    private MemberUtil memberUtil;

    @Test
    @DisplayName("현재 로그인한 회원 ID는 1이다")
    void shouldReturnMemberIdOneWhenLoggedIn() {
        // given & when
        Member currentMember = memberUtil.getCurrentMember();

        // then
        assertEquals(1L, currentMember.getId());
    }
}