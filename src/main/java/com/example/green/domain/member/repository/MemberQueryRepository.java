package com.example.green.domain.member.repository;

import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.page.PageTemplate;

public interface MemberQueryRepository {

	PageTemplate<MemberPointsDto> searchMemberBasicInfo(BasicInfoSearchCondition condition);
}
