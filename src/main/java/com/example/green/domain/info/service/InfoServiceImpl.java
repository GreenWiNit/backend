package com.example.green.domain.info.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.info.domain.InfoEntity;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchListResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchResponseByUser;
import com.example.green.domain.info.exception.InfoException;
import com.example.green.domain.info.exception.InfoExceptionMessage;
import com.example.green.domain.info.repository.InfoRepository;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

	private final InfoRepository infoRepository;
	private final FileClient fileClient;

	// TODO [확인필요] 사이즈가 0일때는 프론트 처리

	@Override
	public PageTemplate<InfoSearchResponseByAdmin> getInfosForAdmin(int page, int size) {
		// 1. 전체 데이터 수 조회
		long totalElements = infoRepository.count();

		// 2. Pagination 객체 생성 (null 처리 및 기본값 설정 포함)
		Pagination pagination = Pagination.of(totalElements, page, size);

		// 3. 실제 데이터 조회 (offset, limit 사용)
		List<InfoEntity> infoList = infoRepository.findAllWithPagination(
			pagination.calculateOffset(),
			pagination.getPageSize()
		);

		// 4. DTO 변환
		List<InfoSearchResponseByAdmin> result = infoList.stream()
			.map(InfoSearchResponseByAdmin::from)
			.toList();

		// 5. PageTemplate 생성
		return PageTemplate.of(result, pagination);
	}

	@Override
	@Transactional(readOnly = true)
	public InfoDetailResponseByAdmin getInfoDetailForAdmin(String infoId) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	@Override
	public InfoDetailResponseByAdmin saveInfo(InfoRequest saveRequest) {
		InfoEntity infoEntity = infoRepository.save(saveRequest.toEntity());
		fileClient.confirmUsingImage(saveRequest.imageUrl());
		log.info("[InfoServiceImpl] 정보공유 등록합니다. 정보공유 번호: {}", infoEntity.getId());
		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	@Override
	public InfoDetailResponseByAdmin updateInfo(String infoId, InfoRequest updateRequest) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		String formerImageUrl = infoEntity.getImageUrl();
		String newImageUrl = updateRequest.imageUrl();

		log.info("[InfoServiceImpl] 정보공유 수정합니다. 정보공유 번호: {}", infoEntity.getId());
		makeUpdateEntity(updateRequest, infoEntity);

		checkWhetherImageChanged(formerImageUrl, newImageUrl);

		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	private void checkWhetherImageChanged(String formerImageUrl, String newImageUrl) {
		// 방어 로직: DB 제약상 formerImageUrl이 null일 가능성은 낮음
		if (StringUtils.isEmpty(formerImageUrl)) {
			fileClient.confirmUsingImage(newImageUrl);
			return;
		}

		// 이미지가 변경된 경우
		if (!formerImageUrl.equals(newImageUrl)) {
			fileClient.unUseImage(formerImageUrl);
			fileClient.confirmUsingImage(newImageUrl);
		}
	}

	@Override
	public void deleteInfo(String infoId) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		log.info("[InfoServiceImpl] 정보공유 삭제합니다. 정보공유 번호: {}", infoEntity.getId());
		infoEntity.markDeleted();
		fileClient.unUseImage(infoEntity.getImageUrl());
	}

	@Override
	public List<InfoSearchResponseByAdmin> getInfosForExcel() {
		List<InfoEntity> infoList = infoRepository.findAllByOrderByCreatedDateDesc();
		List<InfoSearchResponseByAdmin> excelInfoList = infoList.stream()
			.map(InfoSearchResponseByAdmin::from)
			.toList();
		return excelInfoList;
	}

	@Override
	@Transactional(readOnly = true)
	public InfoSearchListResponseByUser getInfosForUser() {
		// 전시중인(Y) 정보만 조회
		List<InfoEntity> infoList = infoRepository.findAllDisplayedInfoForUserOrderByCreatedDateDesc();
		return new InfoSearchListResponseByUser(
			infoList.stream()
				.map(InfoSearchResponseByUser::from)
				.toList()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public InfoDetailResponseByUser getInfoDetailForUser(String id) {
		InfoEntity infoEntity = getInfoEntity(id);
		return InfoDetailResponseByUser.from(infoEntity);
	}

	private InfoEntity getInfoEntity(String infoId) {
		InfoEntity infoEntity = infoRepository.findById(infoId)
			.orElseThrow(() -> new InfoException(InfoExceptionMessage.INVALID_INFO_NUMBER));
		return infoEntity;
	}

	private void makeUpdateEntity(InfoRequest updateRequest, InfoEntity infoEntity) {
		infoEntity.update(
			updateRequest.title(),
			updateRequest.content(),
			updateRequest.infoCategory(),
			updateRequest.imageUrl(),
			updateRequest.isDisplay()
		);
	}

}
