package com.example.green.global.utils;

import org.springframework.stereotype.Component;

import com.example.green.domain.member.Member;
import com.example.green.domain.member.Profile;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;

    // TODO: 개발 단계에서만 사용하는 Mock 데이터 삽입 메서드
    private void insertMockMemberIfNotExist() {
        if (memberRepository.count() != 0) {
            return;
        }

        Member member = Member.createNormalMember(
                new Profile("testNickname", "testImageUrl"));

        memberRepository.save(member);
    }

    public Member getCurrentMember() {
        // TODO: 개발 단계에서만 Mock 데이터 삽입
        insertMockMemberIfNotExist();

        return memberRepository.findById(securityUtil.getCurrentMemberId())
                .orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));
    }

    public Long getCurrentMemberId() {
        return securityUtil.getCurrentMemberId();
    }
} 