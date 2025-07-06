package com.example.green.domain.info.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.info.domain.InfoEntity;
import com.example.green.domain.info.dto.InfoPage;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchListResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchListResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchResponseByUser;
import com.example.green.domain.info.exception.InfoException;
import com.example.green.domain.info.exception.InfoExceptionMessage;
import com.example.green.domain.info.repository.InfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *  “단일 책임 원칙(SRP)” 과 “의도 드러내기(가독성)” 를 최대한 반영하려 함
 * */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

	private final InfoRepository infoRepository;
	private final FileManager fileManager;

	// 데이터 조회 +   DTO 매핑
	@Transactional(readOnly = true)
	public Page<InfoSearchResponseByAdmin> fetchInfoDtos(Pageable pageable) {
		return infoRepository.findAll(pageable)
			.map(InfoSearchResponseByAdmin::from);
	}

	// TODO [확인필요] 사이즈가 0일때는 프론트 처리
	// 각 API 용도별 레퍼 메서드 (SRP)
	@Override
	public InfoSearchListResponseByAdmin getInfosForAdmin(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<InfoSearchResponseByAdmin> dtoPage = fetchInfoDtos(pageable);

		InfoPage pageInfo = new InfoPage(
			dtoPage.getTotalElements(),
			dtoPage.getTotalPages()
		);

		return new InfoSearchListResponseByAdmin(dtoPage.getContent(), pageInfo);
	}

	@Override
	public List<InfoSearchResponseByAdmin> getInfosForExcel(Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		return fetchInfoDtos(pageable).getContent();
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
		fileManager.confirmUsingImage(saveRequest.imageUrl());
		log.info("[InfoServiceImpl] 정보공유 등록합니다. 정보공유 번호: {}", infoEntity.getId());
		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	@Override
	public InfoDetailResponseByAdmin updateInfo(String infoId, InfoRequest updateRequest) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		if (StringUtils.isNotEmpty(infoEntity.getImageUrl())) {
			fileManager.unUseImage(infoEntity.getImageUrl());
		}
		log.info("[InfoServiceImpl] 정보공유 수정합니다. 정보공유 번호: {}", infoEntity.getId());
		makeUpdateEntity(updateRequest, infoEntity);
		fileManager.confirmUsingImage(updateRequest.imageUrl());
		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	@Override
	public void deleteInfo(String infoId) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		log.info("[InfoServiceImpl] 정보공유 삭제합니다. 정보공유 번호: {}", infoEntity.getId());
		infoEntity.markDeleted();
		fileManager.unUseImage(infoEntity.getImageUrl());
	}

	@Override
	@Transactional(readOnly = true)
	public InfoSearchListResponseByUser getInfosForUser() {
		List<InfoEntity> infoList = infoRepository.findAllByOrderByCreatedDateDesc();
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
