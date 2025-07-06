package com.example.green.domain.info.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.info.controller.api.InfoResponseMessage;
import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchListResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchListResponseByUser;
import com.example.green.domain.info.service.InfoService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InfoController implements InfoControllerDocs {
	private final InfoService infoService;
	private final ExcelDownloader excelDownloader;

	@AdminApi(reason = "관리자만 정보 공유 목록 조회 가능")
	@GetMapping("/api/admin/info")
	public ApiTemplate<InfoSearchListResponseByAdmin> getInfosForAdmin(
		@RequestParam(name = "page", required = false) Integer page,
		@RequestParam(name = "size", required = false) Integer size) {
		InfoSearchListResponseByAdmin response = infoService.getInfosForAdmin(page, size);
		return ApiTemplate.ok(InfoResponseMessage.INFO_LIST_FOUND, response);
	}

	@AdminApi(reason = "관리자만 정보 공유 상세 조회 가능")
	@GetMapping("/api/admin/info/{infoId}")
	public ApiTemplate<InfoDetailResponseByAdmin> getInfoDetailForAdmin(@PathVariable("infoId") String infoId) {
		InfoDetailResponseByAdmin response = infoService.getInfoDetailForAdmin(infoId);
		return ApiTemplate.ok(InfoResponseMessage.INFO_DETAIL_FOUND, response);
	}

	@AdminApi(reason = "관리자만 정보 공유 등록 가능")
	@PostMapping("/api/admin/info")
	public ApiTemplate<InfoDetailResponseByAdmin> saveInfo(@RequestBody @Valid InfoRequest saveRequest) {
		InfoDetailResponseByAdmin response = infoService.saveInfo(saveRequest);
		return ApiTemplate.ok(InfoResponseMessage.INFO_CREATED, response);
	}

	@AdminApi(reason = "관리자만 정보 공유 수정 가능")
	@PutMapping("/api/admin/info/{infoId}")
	public ApiTemplate<InfoDetailResponseByAdmin> updateInfo(@PathVariable("infoId") String infoId,
		@RequestBody @Valid InfoRequest updateRequest) {
		InfoDetailResponseByAdmin response = infoService.updateInfo(infoId, updateRequest);
		return ApiTemplate.ok(InfoResponseMessage.INFO_UPDATED, response);
	}

	@AdminApi(reason = "관리자만 정보 공유 삭제 가능")
	@DeleteMapping("/api/admin/info/{infoId}")
	public NoContent deleteInfo(@PathVariable("infoId") String infoId) {
		infoService.deleteInfo(infoId);
		return NoContent.ok(InfoResponseMessage.INFO_DELETED);
	}

	@Override
	@AdminApi(reason = "관리자만 정보 공유 엑셀 다운로드 가능")
	@GetMapping("/api/admin/info/excel")
	public void getInfosForExcel(@RequestParam(name = "page", required = false) Integer page,
		@RequestParam(name = "size", required = false) Integer size,
		HttpServletResponse response) {
		List<InfoSearchResponseByAdmin> result = infoService.getInfosForExcel(page, size);
		excelDownloader.downloadAsStream(result, response);
	}

	@PublicApi(reason = "사용자 정보 공유 목록 조회 누구나 가능")
	@GetMapping("/api/user/info")
	public ApiTemplate<InfoSearchListResponseByUser> getInfosForUser() {
		InfoSearchListResponseByUser response = infoService.getInfosForUser();
		return ApiTemplate.ok(InfoResponseMessage.INFO_LIST_FOUND, response);
	}

	@AuthenticatedApi(reason = "로그인한 사용자만 정보 공유 상세 조회 가능")
	@GetMapping("/api/user/info/{infoId}")
	public ApiTemplate<InfoDetailResponseByUser> getInfoDetailForUser(@PathVariable("infoId") String infoId) {
		InfoDetailResponseByUser response = infoService.getInfoDetailForUser(infoId);
		return ApiTemplate.ok(InfoResponseMessage.INFO_DETAIL_FOUND, response);
	}
}
