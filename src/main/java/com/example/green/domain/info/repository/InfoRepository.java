package com.example.green.domain.info.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.info.domain.InfoEntity;

public interface InfoRepository extends JpaRepository<InfoEntity, String> {
	@Query("""
		SELECT i FROM InfoEntity i
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllWithPagination(Pageable pageable);

	List<InfoEntity> findAllByOrderByCreatedDateDesc();

	@Query("""
		SELECT i FROM InfoEntity i
		WHERE i.isDisplay = 'Y'
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllDisplayedInfoForUserOrderByCreatedDateDesc();

}

