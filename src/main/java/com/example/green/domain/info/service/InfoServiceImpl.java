package com.example.green.domain.info.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

	private final InfoRepository infoRepository;
	private final FileManager fileManager;

	// TODO [확인필요] 사이즈가 0일때는 프론트 처리
	@Override
	@Transactional(readOnly = true)
	public InfoSearchListResponseByAdmin getInfosForAdmin(int page, int size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<InfoEntity> infoList = infoRepository.findAll(pageable);

		List<InfoSearchResponseByAdmin> contentResult = infoList.getContent().stream()
			.map(InfoSearchResponseByAdmin::from)
			.toList();

		InfoPage pageResult = new InfoPage(
			infoList.getTotalElements(),
			infoList.getTotalPages()
		);

		return new InfoSearchListResponseByAdmin(contentResult, pageResult);
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
