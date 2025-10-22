package com.example.green.domain.info.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
		long totalElements = infoRepository.count();
		Pagination pagination = Pagination.of(totalElements, page, size);

		Pageable pageable = PageRequest.of(page, pagination.getPageSize());
		List<InfoEntity> infoList = infoRepository.findAllWithPagination(pageable);

		List<InfoSearchResponseByAdmin> result = infoList.stream()
			.map(InfoSearchResponseByAdmin::from)
			.toList();

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

		// 모든 이미지 URL에 대해 사용 확인
		saveRequest.imageUrls().forEach(fileClient::confirmUsingImage);

		log.info("[InfoServiceImpl] 정보공유 등록합니다. 정보공유 번호: {}", infoEntity.getId());
		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	@Override
	public InfoDetailResponseByAdmin updateInfo(String infoId, InfoRequest updateRequest) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		List<String> formerImageUrls = infoEntity.getImageUrls();
		List<String> newImageUrls = updateRequest.imageUrls();

		log.info("[InfoServiceImpl] 정보공유 수정합니다. 정보공유 번호: {}", infoEntity.getId());
		makeUpdateEntity(updateRequest, infoEntity);

		checkWhetherImagesChanged(formerImageUrls, newImageUrls);

		return InfoDetailResponseByAdmin.from(infoEntity);
	}

	private void checkWhetherImagesChanged(List<String> formerUrls, List<String> newUrls) {
		// 삭제된 이미지: formerUrls에는 있는데 newUrls에 없는 것
		List<String> deletedUrls = formerUrls.stream()
			.filter(url -> !newUrls.contains(url))
			.toList();

		// 새로 추가된 이미지: newUrls에는 있는데 formerUrls에 없는 것
		List<String> addedUrls = newUrls.stream()
			.filter(url -> !formerUrls.contains(url))
			.toList();

		// 삭제된 이미지 사용 해제
		deletedUrls.forEach(fileClient::unUseImage);

		// 새로 추가된 이미지 사용 확인
		addedUrls.forEach(fileClient::confirmUsingImage);
	}

	@Override
	public void deleteInfo(String infoId) {
		InfoEntity infoEntity = getInfoEntity(infoId);
		log.info("[InfoServiceImpl] 정보공유 삭제합니다. 정보공유 번호: {}", infoEntity.getId());
		infoEntity.markDeleted();

		// 모든 이미지 URL 사용 해제
		infoEntity.getImageUrls().forEach(fileClient::unUseImage);
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
			updateRequest.imageUrls(),
			updateRequest.isDisplay()
		);
	}

}
