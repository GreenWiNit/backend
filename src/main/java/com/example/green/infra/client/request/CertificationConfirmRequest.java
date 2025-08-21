package com.example.green.infra.client.request;

public record CertificationConfirmRequest(
	Long groupId,
	Long memberId
) {
}