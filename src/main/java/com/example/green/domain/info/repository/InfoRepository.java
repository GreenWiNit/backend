package com.example.green.domain.info.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import com.example.green.domain.info.domain.InfoEntity;

public interface InfoRepository extends JpaRepository<InfoEntity, String> {
	@Query("""
		SELECT i FROM InfoEntity i
		LEFT JOIN FETCH i.images
		WHERE i.id = :id
		""")
	Optional<InfoEntity> findByIdWithImages(String id);
	@Query("""
		SELECT DISTINCT i FROM InfoEntity i
		LEFT JOIN FETCH i.images
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllWithPagination(Pageable pageable);

	@Query("""
		SELECT DISTINCT i FROM InfoEntity i
		LEFT JOIN FETCH i.images
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllByOrderByCreatedDateDesc();

	@Query("""
		SELECT DISTINCT i FROM InfoEntity i
		LEFT JOIN FETCH i.images
		WHERE i.isDisplay = 'Y'
		ORDER BY i.createdDate DESC
		""")
	List<InfoEntity> findAllDisplayedInfoForUserOrderByCreatedDateDesc();

}

