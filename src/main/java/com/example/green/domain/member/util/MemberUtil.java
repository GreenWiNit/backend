package com.example.green.domain.member.util;



import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;

    private void insertMockMemberIfNotExist() {
        if (memberRepository.count() != 0) {
            return;
        }

        Member member = Member.createNormalMember(
                new Profile("testNickname", "testImageUrl"));

        memberRepository.save(member);
    }

    public Member getCurrentMember() {
        insertMockMemberIfNotExist();

        return memberRepository.findById(securityUtil.getCurrentMemberId())
                .orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));
    }

    public Long getCurrentMemberId() {
        return securityUtil.getCurrentMemberId();
    }
}