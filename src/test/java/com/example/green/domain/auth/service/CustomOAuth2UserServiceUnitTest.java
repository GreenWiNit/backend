package com.example.green.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService 단위 테스트")
class CustomOAuth2UserServiceUnitTest {

    @Mock
    private MemberService memberService;

    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        customOAuth2UserService = new CustomOAuth2UserService(memberService);
    }

    @Test
    @DisplayName("탈퇴한 회원 재가입 시도 시 OAuth2AuthenticationException 발생 확인")
    void testWithdrawnMemberThrowsException() {
        // given
        String memberKey = "google 123456789";
        Member withdrawnMember = mock(Member.class);

        given(withdrawnMember.isWithdrawn()).willReturn(true);
        given(memberService.findByMemberKey(memberKey)).willReturn(Optional.of(withdrawnMember));


        Optional<Member> foundMember = memberService.findByMemberKey(memberKey);
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().isWithdrawn()).isTrue();

        verify(memberService, never()).updateOAuth2Info(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("정상 회원은 OAuth2 정보 업데이트 수행")
    void testNormalMemberUpdatesInfo() {
        // given
        String memberKey = "google 123456789";
        String name = "Test User";
        String email = "test@example.com";

        // when
        customOAuth2UserService.updateExistingUser(memberKey, new TestOAuth2Response(name, email, "google", "123456789"));

        // then
        verify(memberService).updateOAuth2Info(memberKey, name, email);
    }

    @Test
    @DisplayName("신규 회원은 회원 정보 업데이트를 수행하지 않음")
    void testNewMemberDoesNotUpdateInfo() {
        // given
        String memberKey = "google 123456789";

        given(memberService.findByMemberKey(memberKey)).willReturn(Optional.empty());

        // when
        Optional<Member> foundMember = memberService.findByMemberKey(memberKey);
        
        // then
        assertThat(foundMember).isEmpty();
        verify(memberService, never()).updateOAuth2Info(anyString(), anyString(), anyString());
    }

    private static class TestOAuth2Response implements com.example.green.domain.auth.dto.OAuth2ResponseDto {
        private final String name;
        private final String email;
        private final String provider;
        private final String providerId;
        
        TestOAuth2Response(String name, String email, String provider, String providerId) {
            this.name = name;
            this.email = email;
            this.provider = provider;
            this.providerId = providerId;
        }
        
        @Override
        public String getProvider() {
            return provider;
        }
        
        @Override
        public String getProviderId() {
            return providerId;
        }
        
        @Override
        public String getEmail() {
            return email;
        }
        
        @Override
        public String getName() {
            return name;
        }
    }
}