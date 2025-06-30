package com.example.green.domain.info.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.info.domain.InfoEntity;

public interface InfoRepository extends JpaRepository<InfoEntity, String> {
	List<InfoEntity> findAllByOrderByCreatedDateDesc();
}

