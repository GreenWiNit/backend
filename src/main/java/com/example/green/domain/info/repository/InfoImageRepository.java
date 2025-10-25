package com.example.green.domain.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.info.domain.InfoImage;

public interface InfoImageRepository extends JpaRepository<InfoImage, Long> {

	//List<InfoImage> findByInfoIdOrderByDisplayOrder(String infoId);

	@Modifying(flushAutomatically = true)
	@Query("UPDATE InfoImage i SET i.deleted = true WHERE i.info.id = :infoId")
	int softDeleteByInfoId(@Param("infoId") String infoId);

	long countByInfoId(String infoId);
}