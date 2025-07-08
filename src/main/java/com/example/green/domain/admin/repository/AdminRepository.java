package com.example.green.domain.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.admin.entity.Admin;
import com.example.green.domain.admin.entity.enums.AdminStatus;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByLoginId(String loginId);

	Optional<Admin> findByLoginIdAndStatus(String loginId, AdminStatus status);
} 