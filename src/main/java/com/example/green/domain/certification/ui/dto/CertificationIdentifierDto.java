package com.example.green.domain.certification.ui.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CertificationVerifyDto(@NotNull @NotEmpty List<Long> certificationIds) {
}
