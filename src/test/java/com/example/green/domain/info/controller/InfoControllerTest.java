package com.example.green.domain.info.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.green.domain.info.controller.api.InfoResponseMessage;
import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.service.InfoService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 정보공유(Info) 관련 프레젠테이션 계층의 단위 테스트를 진행하는 클래스
 * - 1) 요청 바인딩(@Valid) & 검증(@ExceptionHandler) 테스트
 * - 2) 응답 직렬화 테스트 (ApiTemplate, ResponseMessage)
 */
@WebMvcTest(InfoController.class)
class InfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();
	@MockitoBean
	private InfoService infoService;
	@InjectMocks
	private InfoController infoController;

	@MockitoBean
	private ExcelDownloader excelDownloader;

	// 수정 테스트 body에 들어갈 LocalDataTime 직렬화를 위해 JavaTimeModule을 등록하고, 날짜를 타임스탬프로 직렬화하지 않도록 설정
	@BeforeEach
	void setup() {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Nested
	@WithMockUser(roles = "ADMIN")
	class 관리자 {
		@Test
		void 정보_목록_조회() throws Exception {
			// given
			String page = "0";
			String size = "20";

			// when & then
			mockMvc.perform(
					get("/api/admin/info")
						.param("page", page)
						.param("size", size)
						.with(csrf())
						.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("정보공유 목록이 조회되었습니다."));
		}

		@Test
		void 정보_상세_조회() throws Exception {
			// given
			LocalDateTime dummyDate = LocalDateTime.of(2025, 7, 1, 10, 15, 30);
			InfoDetailResponseByAdmin dummyResponse = new InfoDetailResponseByAdmin(
				"P000001",
				"테스트 제목",
				"공지사항",
				"테스트 내용 10자 이상 테스트 작성",
				"imageUrl",
				"adminUser",
				"Y",
				dummyDate,
				dummyDate
			);

			when(infoService.getInfoDetailForAdmin(eq("P000001")))
				.thenReturn(dummyResponse);

			// when
			ApiTemplate<InfoDetailResponseByAdmin> expectedResponse = ApiTemplate.ok(
				InfoResponseMessage.INFO_DETAIL_FOUND,
				dummyResponse
			);

			// then
			mockMvc.perform(get("/api/admin/info/{infoId}", "P000001")
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value(expectedResponse.message()));
		}

		@Test
		void 정보_상세_조회시_게시글_ID가_없다면_NOT_FOUND_예외를_던진다() throws Exception {
			// given
			String infoId = "";

			// when & then
			mockMvc.perform(get("/api/admin/info/{infoId}", infoId)
					.accept(APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		void 정보_삭제_성공() throws Exception {
			// given & when
			String infoId = "P00001";
			doNothing().when(infoService).deleteInfo(infoId);

			// then
			mockMvc.perform(delete("/api/admin/info/{infoId}", infoId)
					.with(
						csrf()) // Spring Security가 안전하지 않은(state-changing) HTTP 메서드(POST, PUT, PATCH, DELETE)에 대해 기본적으로 CSRF로 보호
					.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("정보공유 글이 삭제되었습니다."));
		}

		/**
		 * 정보수정은 기본적으로 정보 등록과 validation이 동일하여 정상동작만 테스트
		 * mockmvc put의 응답 메세지 정상 도출만 확인한다
		 * */
		@Test
		void 정보_수정_성공() throws Exception {
			// given
			InfoRequest request = InfoRequest.builder()
				.title("테스트 제목")
				.content("테스트 내용 총 10자 이상 테스트")
				.infoCategory(InfoCategory.CONTENTS)
				.imageUrl("http://example.com/image.jpg")
				.isDisplay("Y")
				.build();

			LocalDateTime dummyDate = LocalDateTime.of(2025, 7, 1, 10, 15, 30);
			InfoDetailResponseByAdmin dummyUpdatedResponse = new InfoDetailResponseByAdmin(
				"P000001",
				"테스트 제목",
				"공지사항",
				"테스트 내용 10자 이상 테스트 작성",
				"imageUrl",
				"adminUser",
				"Y",
				dummyDate,
				dummyDate
			);

			when(infoService.updateInfo(eq("P000001"), eq(request)))
				.thenReturn(dummyUpdatedResponse);

			// when
			ApiTemplate<InfoDetailResponseByAdmin> expectedResponse = ApiTemplate.ok(
				InfoResponseMessage.INFO_UPDATED,
				dummyUpdatedResponse
			);

			// then
			mockMvc.perform(put("/api/admin/info/{infoId}", "P000001")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(csrf())
				)

				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value(expectedResponse.message()));
		}

		@Test
		void 엑셀_다운로드_성공() throws Exception {
			// given
			List<InfoSearchResponseByAdmin> mockResult = List.of(mock(InfoSearchResponseByAdmin.class));
			when(infoService.getInfosForExcel()).thenReturn(mockResult);

			// when & then
			mockMvc.perform(get("/api/admin/info/excel")
					.with(csrf())
					.contentType(APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
			verify(excelDownloader).downloadAsStream(anyList(), any(HttpServletResponse.class));
		}

		@Nested
		class 정보_등록 {
			@Test
			void 정보_등록_성공() throws Exception {
				// given
				InfoRequest request = InfoRequest.builder()
					.title("테스트 제목")
					.content("테스트 내용 총 10자 이상 테스트")
					.infoCategory(InfoCategory.CONTENTS)
					.imageUrl("http://example.com/image.jpg")
					.isDisplay("Y")
					.build();

				// when & then
				mockMvc.perform(
						post("/api/admin/info")
							.contentType(APPLICATION_JSON)
							.with(csrf())
							.content(objectMapper.writeValueAsString(request))
					)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.success").value(true))
					.andExpect(jsonPath("$.message").value("정보공유 글이 생성되었습니다."));
			}

			@ParameterizedTest
			@CsvSource({
				"0, false",    // 길이 0 → 유효하지 않음
				"1, true",    // 길이 1 → 유효
				"30, true",   // 길이 30 → 유효
				"31, false"   // 길이 31 → 유효하지 않음
			})
			void 제목은_최소_1자_이상_최대_30자_이하여야하며_VALIDATION_예외를_던진다(int length, boolean isValid) throws Exception {
				// given
				InfoRequest request = buildInfoRequestWithTitleLength(length);

				// when
				ResultActions actions = performInfoPost(request);

				// then
				if (isValid) {
					actions
						.andExpect(status().isOk());
				} else {
					actions
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.success").value(false))
						.andExpect(jsonPath("$.errors[0].fieldName").value("title"))
						.andExpect(jsonPath("$.errors[0].message").value("제목은 최소 1자 이상, 최대 30자 이하까지 등록할 수 있습니다."));
				}
			}

			@ParameterizedTest
			@CsvSource({
				"0, false",    // 길이 0 → 유효하지 않음
				"9, false",    // 길이  → 유효하지 않음
				"10, true",    // 길이 10 → 유효
				"1000, true",   // 길이 1000 → 유효
				"1001, false"   // 길이 1001 → 유효하지 않음
			})
			void 내용은_최소_10자_이상_최대_1000자_이하_여야하며_VALIDATION_예외를_던진다(int length, boolean isValid) throws Exception {
				// given
				InfoRequest request = buildInfoRequestWithContentLength(length);

				// when
				ResultActions actions = performInfoPost(request);

				// then
				if (isValid) {
					actions
						.andExpect(status().isOk());
				} else {
					actions
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.success").value(false))
						.andExpect(jsonPath("$.errors[0].fieldName").value("content"))
						.andExpect(jsonPath("$.errors[0].message").value("내용은 최소 10자 이상, 최대 1000자 이하까지 등록할 수 있습니다."));
				}
			}

			/**
			 * @RequestBody의 HttpMessageConverter 를 테스트 하기 위해 json 형식으로 요청을 보낸다.
			 * */
			@Test
			void 정보_카테고리ENUM에_등록되지_않은_값이_들어왔을_때_DATA_NOT_READABLE_MESSAGE를_던진다() throws Exception {
				// given
				String json = String.format(
					"{\"title\":\"%s\",\"content\":\"%s\",\"infoCategory\":\"%s\",\"imageUrl\":\"%s\",\"isDisplay\":\"%s\"}",
					"테스트 제목",
					"테스트 내용 총 10자 이상 테스트",
					"NOT_EXISTING",
					"http://example.com/image.jpg",
					"Y");

				// when & then
				mockMvc.perform(
						post("/api/admin/info")
							.contentType(APPLICATION_JSON)
							.with(csrf())
							.content(json)
					)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.success").value(false))
					// Jackson 예외를 HttpMessageNotReadableException로 Wrapping
					.andExpect(jsonPath("$.message").value("읽을 수 없는 응답 데이터입니다."));
			}

			@Test
			void 이미지는_첨부되지_않은_경우_VALIDATION_예외를_던진다() throws Exception {
				// given
				InfoRequest request = InfoRequest.builder()
					.title("테스트 제목")
					.content("테스트 내용 총 10자 이상 테스트")
					.infoCategory(InfoCategory.CONTENTS)
					.imageUrl("")
					.isDisplay("Y")
					.build();

				// when & then
				mockMvc.perform(
						post("/api/admin/info")
							.with(csrf())
							.contentType(APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request))
					)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.success").value(false))
					.andExpect(jsonPath("$.errors[0].fieldName").value("imageUrl"))
					.andExpect(jsonPath("$.errors[0].message")
						.value("이미지가 첨부되지 않았습니다."));
			}

			@Test
			void 전시여부가_없는_경우_VALIDATION_예외를_던진다() throws Exception {
				// given
				InfoRequest request = InfoRequest.builder()
					.title("테스트 제목")
					.content("테스트 내용 총 10자 이상 테스트")
					.infoCategory(InfoCategory.CONTENTS)
					.imageUrl("http://example.com/image.jpg")
					.isDisplay("")
					.build();

				// when & then
				mockMvc.perform(
						post("/api/admin/info")
							.with(csrf())
							.contentType(APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request))
					)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.success").value(false))
					.andExpect(jsonPath("$.errors[0].fieldName").value("isDisplay"))
					.andExpect(jsonPath("$.errors[0].message")
						.value("전시여부를 선택해주세요."));
			}

			private InfoRequest buildInfoRequestWithTitleLength(int length) {
				return InfoRequest.builder()
					.title("x".repeat(length))
					.content("테스트 내용 총 10자 이상 테스트")
					.infoCategory(InfoCategory.CONTENTS)
					.imageUrl("http://example.com/image.jpg")
					.isDisplay("Y")
					.build();
			}

			private InfoRequest buildInfoRequestWithContentLength(int length) {
				return InfoRequest.builder()
					.title("테스트 제목")
					.content("x".repeat(length))
					.infoCategory(InfoCategory.CONTENTS)
					.imageUrl("http://example.com/image.jpg")
					.isDisplay("Y")
					.build();
			}

			private ResultActions performInfoPost(InfoRequest request) throws Exception {
				return mockMvc.perform(
					post("/api/admin/info")
						.with(csrf())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andDo(print());
			}
		}

	}

	@Nested
	@WithMockUser(roles = "USER")
	class 사용자 {
		@Test
		void 정보_목록_조회() throws Exception {
			// given
			String page = "0";
			String size = "20";

			// when & then
			mockMvc.perform(
					get("/api/user/info")
						.param("page", page)
						.param("size", size)
						.with(csrf())
						.contentType(APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("정보공유 목록이 조회되었습니다."));
		}

		@Test
		void 정보_상세_조회() throws Exception {
			// given

			InfoDetailResponseByUser dummyResponse = new InfoDetailResponseByUser(
				"P000001",
				"테스트 제목",
				"공지사항",
				"테스트 내용 10자 이상 테스트 작성"
			);

			when(infoService.getInfoDetailForUser(eq("P000001")))
				.thenReturn(dummyResponse);

			// when
			ApiTemplate<InfoDetailResponseByUser> expectedResponse = ApiTemplate.ok(
				InfoResponseMessage.INFO_DETAIL_FOUND,
				dummyResponse
			);

			// then
			mockMvc.perform(get("/api/user/info/{infoId}", "P000001")
					.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value(expectedResponse.message()));
		}
	}
}
