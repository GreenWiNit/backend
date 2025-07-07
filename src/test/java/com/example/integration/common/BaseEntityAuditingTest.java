package com.example.integration.common;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.security.PrincipalDetails;

@SpringBootTest(classes = com.example.green.GreenApplication.class)
@ActiveProfiles("test")
@Transactional
class BaseEntityAuditingTest extends BaseIntegrationTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("인증된 사용자로 엔티티 생성 시 Auditing 정보가 한국시간으로 설정된다")
	void shouldSetKoreanTimeWhenCreateEntityWithAuthentication() {
		// given - 인증된 사용자 설정
		String testUsername = "google 123456789";
		String testName = "테스트유저";
		PrincipalDetails principal = new PrincipalDetails(1L, testUsername, "ROLE_USER", testName);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
			principal, null, principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);

		LocalDateTime beforeCreate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

		// when - 엔티티 생성
		Member member = Member.create(testUsername, testName, "test@email.com");
		Member savedMember = memberRepository.saveAndFlush(member);

		LocalDateTime afterCreate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

		// then - Auditing 정보 검증
		assertNotNull(savedMember.getCreatedDate(), "생성일시가 설정되어야 함");
		assertNotNull(savedMember.getModifiedDate(), "수정일시가 설정되어야 함");
		assertNotNull(savedMember.getCreatedBy(), "생성자가 설정되어야 함");
		assertNotNull(savedMember.getLastModifiedBy(), "수정자가 설정되어야 함");

		// 한국시간 범위 내에 있는지 확인
		assertTrue(savedMember.getCreatedDate().isAfter(beforeCreate.minusSeconds(1)), 
			"생성일시가 테스트 시작 시간 이후여야 함");
		assertTrue(savedMember.getCreatedDate().isBefore(afterCreate.plusSeconds(1)), 
			"생성일시가 테스트 종료 시간 이전이어야 함");

		// 생성일시와 수정일시가 동일해야 함 (새로 생성된 경우)
		assertEquals(savedMember.getCreatedDate(), savedMember.getModifiedDate(),
			"생성 시 생성일시와 수정일시가 동일해야 함");

		// 사용자 정보 확인
		assertEquals(testUsername, savedMember.getCreatedBy(), "생성자가 현재 인증된 사용자여야 함");
		assertEquals(testUsername, savedMember.getLastModifiedBy(), "수정자가 현재 인증된 사용자여야 함");

		System.out.println("JPA Auditing 결과");
		System.out.println("생성일시: " + savedMember.getCreatedDate());
		System.out.println("수정일시: " + savedMember.getModifiedDate());
		System.out.println("생성자: " + savedMember.getCreatedBy());
		System.out.println("수정자: " + savedMember.getLastModifiedBy());
		System.out.println("현재 한국시간: " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));
	}

	@Test
	@DisplayName("엔티티 수정 시 수정자와 수정일시만 업데이트된다")
	void shouldUpdateLastModifiedInfoWhenEntityUpdated() throws InterruptedException {
		// given - 첫 번째 사용자로 엔티티 생성
		String firstUser = "google 111111111";
		PrincipalDetails firstPrincipal = new PrincipalDetails(1L, firstUser, "ROLE_USER", "첫번째유저");
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(firstPrincipal, null, firstPrincipal.getAuthorities()));

		Member member = Member.create(firstUser, "첫번째유저", "first@email.com");
		Member savedMember = memberRepository.saveAndFlush(member);
		
		LocalDateTime originalCreatedDate = savedMember.getCreatedDate();
		String originalCreatedBy = savedMember.getCreatedBy();

		// 시간 차이를 만들기 위해 잠시 대기
		Thread.sleep(100);

		// when - 두 번째 사용자로 엔티티 수정
		String secondUser = "kakao 222222222";
		PrincipalDetails secondPrincipal = new PrincipalDetails(2L, secondUser, "ROLE_USER", "두번째유저");
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(secondPrincipal, null, secondPrincipal.getAuthorities()));

		LocalDateTime beforeUpdate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		savedMember.updateNickname("수정된이름");
		Member updatedMember = memberRepository.saveAndFlush(savedMember);
		LocalDateTime afterUpdate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

		// then - 수정 정보만 변경되었는지 확인
		assertEquals(originalCreatedDate, updatedMember.getCreatedDate(), "생성일시는 변경되지 않아야 함");
		assertEquals(originalCreatedBy, updatedMember.getCreatedBy(), "생성자는 변경되지 않아야 함");

		assertTrue(updatedMember.getModifiedDate().isAfter(originalCreatedDate), 
			"수정일시가 생성일시보다 늦어야 함");
		assertTrue(updatedMember.getModifiedDate().isAfter(beforeUpdate.minusSeconds(1)), 
			"수정일시가 업데이트 시작 시간 이후여야 함");
		assertTrue(updatedMember.getModifiedDate().isBefore(afterUpdate.plusSeconds(1)), 
			"수정일시가 업데이트 종료 시간 이전이어야 함");

		assertEquals(secondUser, updatedMember.getLastModifiedBy(), "수정자가 현재 인증된 사용자여야 함");

		System.out.println("=== 수정 후 JPA Auditing 결과 ===");
		System.out.println("생성일시: " + updatedMember.getCreatedDate() + " (변경 안됨)");
		System.out.println("수정일시: " + updatedMember.getModifiedDate() + " (업데이트됨)");
		System.out.println("생성자: " + updatedMember.getCreatedBy() + " (변경 안됨)");
		System.out.println("수정자: " + updatedMember.getLastModifiedBy() + " (업데이트됨)");
	}

	@Test
	@DisplayName("인증되지 않은 상태에서 엔티티 생성 시 SYSTEM으로 설정된다")
	void shouldSetSystemWhenCreateEntityWithoutAuthentication() {
		// given - 인증 정보 제거
		SecurityContextHolder.clearContext();

		// when - 엔티티 생성
		Member member = Member.create("test", "테스트", "test@email.com");
		Member savedMember = memberRepository.saveAndFlush(member);

		// then - SYSTEM으로 설정되었는지 확인
		assertEquals("SYSTEM", savedMember.getCreatedBy(), "인증되지 않은 경우 생성자가 SYSTEM이어야 함");
		assertEquals("SYSTEM", savedMember.getLastModifiedBy(), "인증되지 않은 경우 수정자가 SYSTEM이어야 함");
		
		assertNotNull(savedMember.getCreatedDate(), "생성일시는 설정되어야 함");
		assertNotNull(savedMember.getModifiedDate(), "수정일시는 설정되어야 함");

		System.out.println("=== 인증 없이 생성한 결과 ===");
		System.out.println("생성자: " + savedMember.getCreatedBy());
		System.out.println("수정자: " + savedMember.getLastModifiedBy());
	}

	@Test
	@DisplayName("한국시간(Asia/Seoul)이 정확히 적용되는지 확인")
	void shouldApplyKoreanTimeZone() {
		// given
		String testUsername = "naver 555555555";
		PrincipalDetails principal = new PrincipalDetails(5L, testUsername, "ROLE_USER", "시간테스트유저");
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

		LocalDateTime koreaTimeBeforeCreate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		LocalDateTime utcTimeBeforeCreate = LocalDateTime.now(ZoneId.of("UTC"));

		// when
		Member member = Member.create(testUsername, "시간테스트유저", "timezone@test.com");
		Member savedMember = memberRepository.saveAndFlush(member);

		LocalDateTime koreaTimeAfterCreate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

		// then - 한국시간 기준으로 설정되었는지 확인
		LocalDateTime createdTime = savedMember.getCreatedDate();
		
		assertTrue(createdTime.isAfter(koreaTimeBeforeCreate.minusSeconds(1)), 
			"생성시간이 한국시간 기준 테스트 시작 시간 이후여야 함");
		assertTrue(createdTime.isBefore(koreaTimeAfterCreate.plusSeconds(1)), 
			"생성시간이 한국시간 기준 테스트 종료 시간 이전이어야 함");

		System.out.println("시간대 확인");
		System.out.println("테스트 시작 (한국시간): " + koreaTimeBeforeCreate);
		System.out.println("테스트 시작 (UTC): " + utcTimeBeforeCreate);
		System.out.println("DB 저장 시간: " + createdTime);
		System.out.println("테스트 종료 (한국시간): " + koreaTimeAfterCreate);
		System.out.println("한국시간과 UTC 차이: " + (koreaTimeBeforeCreate.getHour() - utcTimeBeforeCreate.getHour()) + "시간");
	}
} 