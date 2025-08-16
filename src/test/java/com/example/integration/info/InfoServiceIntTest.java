package com.example.integration.info;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.info.domain.InfoEntity;
import com.example.green.domain.info.domain.vo.InfoCategory;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.exception.InfoException;
import com.example.green.domain.info.exception.InfoExceptionMessage;
import com.example.green.domain.info.repository.InfoRepository;
import com.example.green.domain.info.service.InfoService;
import com.example.integration.common.BaseIntegrationTest;

/**
 * 정보공유(Info) 관련 비즈니스 계층의 통합 테스트를 진행하는 테스트 클래스
 * - Repository 단은 별도로 테스트하지 않음 (Hibernate 외부 시스템 사용)
 * - Service 단 단위테스트는 통합테스트로 대체 (응답값 검증 위주)
 */
class InfoServiceIntTest extends BaseIntegrationTest {

	@Autowired
	private InfoRepository infoRepository;
	@Autowired
	private InfoService infoService;
	@MockitoSpyBean
	private FileManager fileManager;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static InfoRequest createSaveInfoRequest() {
		return InfoRequest.builder()
			.title("title")
			.content("content")
			.infoCategory(InfoCategory.CONTENTS)
			.imageUrl("imageUrl")
			.isDisplay("Y")
			.build();
	}

	private static InfoRequest createUpdateInfoRequest() {
		return InfoRequest.builder()
			.title("updateTitle")
			.content("updateContent")
			.infoCategory(InfoCategory.ETC)
			.imageUrl("updateImageUrl")
			.isDisplay("N")
			.build();
	}

	@AfterEach
	void tearDown() {
		infoRepository.deleteAllInBatch();
	}

	@Nested
	class 관리자 {
		@Nested
		class 디폴트_Entity_생성 {
			private InfoEntity infoEntity;

			@BeforeEach
			void setup() {
				InfoRequest infoRequest = createSaveInfoRequest();
				infoEntity = infoRepository.save(infoRequest.toEntity());
			}

			@Test
			void 정보_목록을_조회한다() {
				// given
				int page = 0;
				int size = 10;

				// when
				var response = infoService.getInfosForAdmin(page, size);

				// then
				assertThat(response.content()).isNotEmpty();
				assertThat(response.totalPages()).isGreaterThan(0);
				// Enum 타입이 String으로 변환되어 반환되는지 확인
				assertThat(response.content().get(0).infoCategoryName()).isEqualTo(
					infoEntity.getInfoCategory().getDescription());
			}

			@Test
			void 정보_목록을_조회할_때에는_삭제된_정보는_포함되지_않는다() {
				// given
				infoEntity.markDeleted();
				infoRepository.save(infoEntity);

				InfoRequest infoRequest2 = createSaveInfoRequest();
				infoRepository.save(infoRequest2.toEntity());

				int page = 0;
				int size = 10;

				// when
				var response = infoService.getInfosForAdmin(page, size);

				// then
				assertThat(response.content()).size().isEqualTo(1);
			}

			@Test
			void 정보_목록_조회가_등록일을_기준으로_내림차순_정렬로_조회된다() {
				// given
				InfoRequest infoRequest2 = createSaveInfoRequest();
				InfoEntity infoEntity2 = infoRepository.save(infoRequest2.toEntity());

				InfoRequest infoRequest3 = createSaveInfoRequest();
				InfoEntity infoEntity3 = infoRepository.save(infoRequest3.toEntity());

				int page = 0;
				int size = 10;

				// when
				var response = infoService.getInfosForAdmin(page, size);

				// then
				assertThat(response.content()).hasSize(3);
				assertThat(response.content().get(0).id()).isEqualTo(infoEntity3.getId());
				assertThat(response.content().get(1).id()).isEqualTo(infoEntity2.getId());
			}

			@Test
			void 정보_상세를_조회한다() {
				// given
				String infoId = infoEntity.getId();

				// when
				var response = infoService.getInfoDetailForAdmin(infoId);

				// then
				assertThat(response.title()).isEqualTo(infoEntity.getTitle());
				assertThat(response.infoCategoryCode()).isEqualTo(infoEntity.getInfoCategory().name());
			}

			@ParameterizedTest
			@ValueSource(strings = {"notExitsId", ""})
			void 정보_상세를_찾을_수_없다면_예외를_던진다(String infoId) {
				// TODO [추후작업필요] controller 에서 변수로 받는 id 값 null 처리 해줘야 required = true (서비스까지 넘어오지 못하게)
				// when & then
				assertThatThrownBy(() -> infoService.getInfoDetailForAdmin(infoId))
					.isInstanceOf(InfoException.class)
					.hasFieldOrPropertyWithValue("exceptionMessage", InfoExceptionMessage.INVALID_INFO_NUMBER);
			}

			@Test
			void 정보를_수정한다() {
				// TODO [리펙토링필요] -> 시큐리티 어노테이션 완성되면 실제 인입되는 ID로 변경 테스트
				// given
				InfoRequest updateRequest = createUpdateInfoRequest();
				Mockito.doNothing().when(fileManager).unUseImage(Mockito.anyString());
				Mockito.doNothing().when(fileManager).confirmUsingImage(Mockito.anyString());

				// when
				var response = infoService.updateInfo(infoEntity.getId(), updateRequest);

				// then
				assertThat(response.title()).isEqualTo(updateRequest.title());
				assertThat(response.infoCategoryCode()).isEqualTo("ETC"); // 컨텐츠 -> 기타
				assertThat(response.infoCategoryName()).isEqualTo("기타");
				assertThat(response.imageurl()).isEqualTo(updateRequest.imageUrl());
			}

			@Test
			void 정보_수정시_이미지URL이_같으면_기존_이미지_URL을_유지한다() {
				// given
				InfoRequest updateRequest = InfoRequest.builder()
					.title("updateTitle")
					.content("updateContent")
					.infoCategory(InfoCategory.ETC)
					.imageUrl(infoEntity.getImageUrl()) // 기존 이미지 URL과 동일
					.isDisplay("N")
					.build();

				// when
				var response = infoService.updateInfo(infoEntity.getId(), updateRequest);

				// then - 결과 값 검증
				assertThat(response.imageurl()).isEqualTo(infoEntity.getImageUrl());

				// then - 메서드 호출 여부 검증
				verify(fileManager, never()).unUseImage(anyString());
				verify(fileManager, never()).confirmUsingImage(anyString());
			}

			@Test
			void 정보를_삭제한다() {
				// given
				String deleteInfoId = infoEntity.getId();
				Mockito.doNothing().when(fileManager).unUseImage(Mockito.anyString());

				// when
				infoService.deleteInfo(deleteInfoId);

				// given
				assertThatThrownBy(() -> infoService.getInfoDetailForAdmin(deleteInfoId))
					.isInstanceOf(InfoException.class)
					.hasFieldOrPropertyWithValue("exceptionMessage", InfoExceptionMessage.INVALID_INFO_NUMBER);
			}
		}

		@Nested
		class 디폴트_Entity_생성하지_않음 {
			@BeforeEach
			void cleanUpBefore() {
				jdbcTemplate.execute("TRUNCATE TABLE INFO");
			}

			@Test
			void 정보를_등록한다() {
				// TODO [리펙토링필요] -> 시큐리티 어노테이션 완성되면 사용자ID(lastModifiedBy) 값이 제대로 반영되는지 확인
				// TODO [확인필요] SpyBean 사용하여 건너띄는 방식으로 FileService 의존성 분리 -> 더 좋은 방법이 있을지
				// given
				InfoRequest saveRequest = createSaveInfoRequest();
				Mockito.doNothing().when(fileManager).confirmUsingImage(Mockito.anyString());

				// when
				var response = infoService.saveInfo(saveRequest);

				//then
				assertThat(response.id()).isEqualTo("P000001");
				assertThat(response.title()).isEqualTo(saveRequest.title());
				assertThat(response.modifiedDate()).isNotNull(); // 수정일자 확인
			}
		}
	}

	@Nested
	class 사용자 {
		private InfoEntity infoEntity;

		@BeforeEach
		void setup() {
			InfoRequest infoRequest = createSaveInfoRequest();
			infoEntity = infoRepository.save(infoRequest.toEntity());
		}

		@Test
		void 정보_목록을_조회한다() {

			// when
			var response = infoService.getInfosForUser();

			// then
			assertThat(response.content()).isNotEmpty();
			assertThat(response.content().get(0).infoCategoryName()).isEqualTo(
				infoEntity.getInfoCategory().getDescription());
		}

		@Test
		void 정보_목록을_조회할_때에는_삭제된_정보는_포함되지_않는다() {
			// given
			InfoRequest infoRequest = createUpdateInfoRequest();

			InfoEntity infoEntityToDelete = infoRequest.toEntity();
			infoEntityToDelete.markDeleted();

			infoRepository.save(infoEntityToDelete);

			// when
			var response = infoService.getInfosForUser();

			// then
			assertThat(response.content()).size().isEqualTo(1);
		}

		@Test
		void 정보_목록을_조회할_때에는_미전시_정보는_포함되지_않는다() {
			// given
			InfoRequest infoRequest = createUpdateInfoRequest();
			infoRepository.save(infoRequest.toEntity());

			// when
			var response = infoService.getInfosForUser();

			// then
			assertThat(response.content()).size().isEqualTo(1);
		}

		@Test
		void 정보_상세를_조회한다() {
			// when
			var response = infoService.getInfoDetailForUser(infoEntity.getId());

			// then
			assertThat(response.title()).isEqualTo(infoEntity.getTitle());
			assertThat(response.infoCategoryName()).isEqualTo(
				infoEntity.getInfoCategory().getDescription());
		}
	}
}