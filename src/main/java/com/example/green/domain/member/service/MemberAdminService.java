package com.example.green.domain.member.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.dto.admin.MemberListRequestDto;
import com.example.green.domain.member.dto.admin.MemberListResponseDto;
import com.example.green.domain.member.dto.admin.WithdrawnMemberListResponseDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAdminService {

	private final MemberRepository memberRepository;
	private final MemberService memberService;

	/**
	 * 관리자용 회원 목록 조회 (페이징)
	 * - 탈퇴하지 않은 회원만 조회
	 * - 가입일순 내림차순 정렬
	 */
	public PageTemplate<MemberListResponseDto> getMemberList(MemberListRequestDto request) {
		Page<Member> memberPage = memberRepository.findActiveMembersForAdmin(request.toPageable());
		
		List<MemberListResponseDto> content = memberPage.getContent().stream()
			.map(MemberListResponseDto::from)
			.toList();
		
		log.info("[ADMIN] 회원 목록 조회 완료: page={}, size={}, totalElements={}", 
			request.page(), request.size(), memberPage.getTotalElements());
		
		return new PageTemplate<MemberListResponseDto>(
			memberPage.getTotalElements(),
			memberPage.getTotalPages(),
			memberPage.getNumber(),
			memberPage.getSize(),
			memberPage.hasNext(),
			content
		);
	}

	/**
	 * 관리자용 회원 전체 목록 조회 (엑셀 다운로드용)
	 * - 탈퇴하지 않은 회원만 조회
	 * - 가입일순 내림차순 정렬
	 */
	public List<MemberListResponseDto> getAllMembersForExcel() {
		List<Member> members = memberRepository.findAllActiveMembersForAdmin();
		
		List<MemberListResponseDto> result = members.stream()
			.map(MemberListResponseDto::from)
			.toList();
		
		log.info("[ADMIN] 회원 엑셀 다운로드용 전체 목록 조회 완료: count={}", result.size());
		return result;
	}

	/**
	 * 관리자용 탈퇴 회원 목록 조회 (페이징)
	 * - 탈퇴한 회원만 조회
	 * - 탈퇴일순 내림차순 정렬
	 */
	public PageTemplate<WithdrawnMemberListResponseDto> getWithdrawnMemberList(MemberListRequestDto request) {
		Page<Member> memberPage = memberRepository.findWithdrawnMembersForAdmin(request.toPageable());
		
		List<WithdrawnMemberListResponseDto> content = memberPage.getContent().stream()
			.map(WithdrawnMemberListResponseDto::from)
			.toList();
		
		log.info("[ADMIN] 탈퇴 회원 목록 조회 완료: page={}, size={}, totalElements={}", 
			request.page(), request.size(), memberPage.getTotalElements());
		
		return new PageTemplate<WithdrawnMemberListResponseDto>(
			memberPage.getTotalElements(),
			memberPage.getTotalPages(),
			memberPage.getNumber(),
			memberPage.getSize(),
			memberPage.hasNext(),
			content
		);
	}

	/**
	 * 관리자용 탈퇴 회원 전체 목록 조회 (엑셀 다운로드용)
	 * - 탈퇴한 회원만 조회
	 * - 탈퇴일순 내림차순 정렬
	 */
	public List<WithdrawnMemberListResponseDto> getAllWithdrawnMembersForExcel() {
		List<Member> members = memberRepository.findAllWithdrawnMembersForAdmin();
		
		List<WithdrawnMemberListResponseDto> result = members.stream()
			.map(WithdrawnMemberListResponseDto::from)
			.toList();
		
		log.info("[ADMIN] 탈퇴 회원 엑셀 다운로드용 전체 목록 조회 완료: count={}", result.size());
		return result;
	}

	/**
	 * 관리자가 회원 강제 삭제 (탈퇴 처리) - username으로 식별
	 * - 성능 최적화: username으로 직접 조회 (UNIQUE 인덱스)
	 * - 소셜 로그인 제공자별 명확한 구분
	 */
	@Transactional
	public void deleteMemberByUsername(String username) {
		log.info("[ADMIN] 관리자에 의한 회원 강제 삭제 요청: username={}", username);
		
		// username으로 회원 조회
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.error("[ADMIN] 존재하지 않는 회원 사용자명: {}", username);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
		
		try {
			memberService.forceDeleteMemberByAdmin(member.getId());
			log.info("[ADMIN] 관리자에 의한 회원 강제 삭제 완료: username={}, memberId={}, email={}", 
				username, member.getId(), member.getEmail());
		} catch (BusinessException e) {
			log.error("[ADMIN] 회원 강제 삭제 실패: username={}, error={}", username, e.getMessage());
			throw e;
		}
	}

	/**
	 * 회원 존재 여부 확인 (관리자용) - username으로 확인
	 */
	public void validateMemberExistsByUsername(String username) {
		if (!memberRepository.existsByUsername(username)) {
			log.error("[ADMIN] 존재하지 않는 회원 사용자명: {}", username);
			throw new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
		}
	}
} 