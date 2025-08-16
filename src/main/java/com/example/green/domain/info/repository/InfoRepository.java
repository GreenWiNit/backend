package com.example.green.domain.info.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.info.domain.InfoEntity;

public interface InfoRepository extends JpaRepository<InfoEntity, String> {
	@Query("""
		SELECT i FROM InfoEntity i 
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllWithPagination(@Param("offset") long offset, @Param("limit") int limit);

	List<InfoEntity> findAllByOrderByCreatedDateDesc();

	// 사용자단 전용: 전시중인 정보만 조회
	@Query("""
		SELECT i FROM InfoEntity i 
		WHERE i.isDisplay = 'Y' 
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllDisplayedInfoForUserOrderByCreatedDateDesc();

}

