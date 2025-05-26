package com.example.green.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.file.domain.FileEntity;

public interface FileJpaRepository extends JpaRepository<FileEntity, Long> {
}
