package com.example.green.domain.member.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.member.dto.admin.MemberListResponseDto;
import com.example.green.domain.member.dto.admin.WithdrawnMemberListResponseDto;
import com.example.green.domain.member.service.MemberAdminService;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.template.base.BaseControllerUnitTest;


@WebMvcTest(MemberAdminController.class)
class MemberAdminControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private MemberAdminService memberAdminService;

	@MockitoBean
	private ExcelDownloader excelDownloader;

	@Test
	@DisplayName("관리자가 회원 목록을 페이징으로 조회할 수 있다")
	void getMemberList_Success() {
		// given
		MemberListResponseDto member = new MemberListResponseDto(
			"naver 123456789", "test@naver.com", "테스트회원", "010-1234-5678", LocalDateTime.now(), "일반회원", "naver"
		);
		PageTemplate<MemberListResponseDto> mockPage = new PageTemplate<>(
			1L, 1, 0, 10, false, List.of(member)
		);
		
		when(memberAdminService.getMemberList(any())).thenReturn(mockPage);

		// when & then
		given().log().all()
			.param("page", "0")
			.param("size", "10")
		.when()
			.get("/api/admin/members")
		.then().log().all()
			.status(HttpStatus.OK)
			.body("success", equalTo(true))
			.body("message", equalTo("회원 목록 조회가 완료되었습니다."))
			.body("result.totalElements", equalTo(1))
			.body("result.content[0].username", equalTo("naver 123456789"))
			.body("result.content[0].email", equalTo("test@naver.com"))
			.body("result.content[0].provider", equalTo("naver"));
	}

	@Test
	@DisplayName("관리자가 회원 목록을 엑셀로 다운로드할 수 있다")
	void downloadMemberListExcel_Success() {
		// given
		List<MemberListResponseDto> members = List.of(
			new MemberListResponseDto("naver 123456789", "test@naver.com", "테스트회원", "010-1234-5678", LocalDateTime.now(), "일반회원", "naver")
		);
		when(memberAdminService.getAllMembersForExcel()).thenReturn(members);

		// when & then
		given().log().all()
		.when()
			.get("/api/admin/members/excel")
		.then().log().all()
			.status(HttpStatus.OK);

		verify(excelDownloader).downloadAsStream(eq(members), any());
	}

	@Test
	@DisplayName("관리자가 회원을 삭제할 수 있다")
	void deleteMember_Success() {
		// given
		String username = "naver 123456789";
		doNothing().when(memberAdminService).validateMemberExistsByMemberKey(username);
		doNothing().when(memberAdminService).deleteMemberByMemberKey(username);

		// when & then
		given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body("""
				{
					"username": "naver 123456789"
				}
				""")
		.when()
			.post("/api/admin/members/delete")
		.then().log().all()
			.status(HttpStatus.OK)
			.body("success", equalTo(true))
			.body("message", equalTo("회원 삭제가 완료되었습니다."));

		verify(memberAdminService).validateMemberExistsByMemberKey(username);
		verify(memberAdminService).deleteMemberByMemberKey(username);
	}

	@Test
	@DisplayName("페이지 파라미터가 없으면 기본값이 적용된다")
	void getMemberList_WithDefaultPagination() {
		// given
		PageTemplate<MemberListResponseDto> mockPage = new PageTemplate<>(
			0L, 0, 0, 10, false, List.of()
		);
		when(memberAdminService.getMemberList(any())).thenReturn(mockPage);

		// when & then
		given().log().all()
		.when()
			.get("/api/admin/members")
		.then().log().all()
			.status(HttpStatus.OK)
			.body("success", equalTo(true));

		verify(memberAdminService).getMemberList(argThat(request -> 
			request.page() == 0 && request.size() == 10));
	}

	@Test
	@DisplayName("존재하지 않는 회원 삭제 시 에러가 발생한다")
	void deleteMember_MemberNotFound() {
		// given
		String username = "invalid_username";
		doThrow(new RuntimeException("해당 회원을 찾을 수 없습니다."))
			.when(memberAdminService).validateMemberExistsByMemberKey(username);

		// when & then
		given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body("""
				{
					"username": "invalid_username"
				}
				""")
		.when()
			.post("/api/admin/members/delete")
		.then().log().all()
			.status(HttpStatus.INTERNAL_SERVER_ERROR);

		verify(memberAdminService).validateMemberExistsByMemberKey(username);
		verify(memberAdminService, never()).deleteMemberByMemberKey(any());
	}

	@Test
	@DisplayName("관리자가 탈퇴 회원 목록을 페이징으로 조회할 수 있다")
	void getWithdrawnMemberList_Success() {
		// given
		WithdrawnMemberListResponseDto member = new WithdrawnMemberListResponseDto(
			"naver 123456789", "test@naver.com", "탈퇴회원", "010-1234-5678", 
			LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), 
			"일반회원", "naver"
		);
		PageTemplate<WithdrawnMemberListResponseDto> mockPage = new PageTemplate<>(
			1L, 1, 0, 10, false, List.of(member)
		);
		
		when(memberAdminService.getWithdrawnMemberList(any())).thenReturn(mockPage);

		// when & then
		given().log().all()
			.param("page", "0")
			.param("size", "10")
		.when()
			.get("/api/admin/members/withdrawn")
		.then().log().all()
			.status(HttpStatus.OK)
			.body("success", equalTo(true))
			.body("message", equalTo("탈퇴 회원 목록 조회가 완료되었습니다."))
			.body("result.totalElements", equalTo(1))
			.body("result.content[0].username", equalTo("naver 123456789"))
			.body("result.content[0].email", equalTo("test@naver.com"))
			.body("result.content[0].provider", equalTo("naver"));
	}

	@Test
	@DisplayName("관리자가 탈퇴 회원 목록을 엑셀로 다운로드할 수 있다")
	void downloadWithdrawnMemberListExcel_Success() {
		// given
		List<WithdrawnMemberListResponseDto> members = List.of(
			new WithdrawnMemberListResponseDto("naver 123456789", "test@naver.com", "탈퇴회원", "010-1234-5678", 
				LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), "일반회원", "naver")
		);
		when(memberAdminService.getAllWithdrawnMembersForExcel()).thenReturn(members);

		// when & then
		given().log().all()
		.when()
			.get("/api/admin/members/withdrawn/excel")
		.then().log().all()
			.status(HttpStatus.OK);

		verify(excelDownloader).downloadAsStream(eq(members), any());
	}
} 