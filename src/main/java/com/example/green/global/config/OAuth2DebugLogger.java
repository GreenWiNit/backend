package com.example.green.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2DebugLogger {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void logOAuth2ClientConfig() {
		log.info("=== OAuth2 Client Registration Debug ===");
		
		try {
			ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
			
			if (googleRegistration != null) {
				log.info("Google Client Registration Found:");
				log.info("  Client ID: {}", maskClientId(googleRegistration.getClientId()));
				log.info("  Client Name: {}", googleRegistration.getClientName());
				log.info("  Redirect URI: {}", googleRegistration.getRedirectUri());
				log.info("  Authorization Grant Type: {}", googleRegistration.getAuthorizationGrantType());
				log.info("  Authorization URI: {}", googleRegistration.getProviderDetails().getAuthorizationUri());
				log.info("  Token URI: {}", googleRegistration.getProviderDetails().getTokenUri());
				log.info("  Scopes: {}", googleRegistration.getScopes());
			} else {
				log.error("Google Client Registration NOT FOUND!");
			}
		} catch (Exception e) {
			log.error("Error loading OAuth2 client registration: {}", e.getMessage(), e);
		}
		
		log.info("=========================================");
	}

	private String maskClientId(String clientId) {
		if (clientId == null || clientId.length() < 10) {
			return "***";
		}
		return clientId.substring(0, 6) + "***" + clientId.substring(clientId.length() - 4);
	}
} 