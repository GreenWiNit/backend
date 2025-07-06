package com.example.green.domain.info.service;

import java.util.List;

import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchListResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchListResponseByUser;

public interface InfoService {
	/**
	 * 관리자 전체 Info 페이지 단위로 조회
	 */
	InfoSearchListResponseByAdmin getInfosForAdmin(int page, int size);

	/**
	 * 관리자 Info 상세 페이지 조회
	 */
	InfoDetailResponseByAdmin getInfoDetailForAdmin(String infoId);

	/**
	 * 관리자 Info 등록
	 */
	InfoDetailResponseByAdmin saveInfo(InfoRequest saveRequest);

	/**
	 * 관리자 Info 수정
	 */
	InfoDetailResponseByAdmin updateInfo(String infoId, InfoRequest updateRequest);

	/**
	 * 관리자 Info 삭제
	 */
	void deleteInfo(String deleteInfoId);

	/**
	 * 관리자 Info 엑셀 다운로드용 전체 조회
	 */
	List<InfoSearchResponseByAdmin> getInfosForExcel();

	/**
	 * 일반 사용자 노출 가능한 Info 전체 조회 (페이징 없음)
	 */
	InfoSearchListResponseByUser getInfosForUser();

	/**
	 * 로그인한 사용자 Info 상세 페이지 조회
	 */
	InfoDetailResponseByUser getInfoDetailForUser(String id);

}
