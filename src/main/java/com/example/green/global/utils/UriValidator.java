package com.example.green.global.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Component;

@Component
public class UriValidator {

	public boolean isValidUri(String uriString) {
		if (uriString == null || uriString.trim().isEmpty()) {
			return false;
		}

		try {
			URI uri = new URI(uriString);
			return uri.isAbsolute();
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public static boolean isAbsoluteUri(String uriString) {
		if (uriString == null || uriString.isEmpty()) {
			return false;
		}

		try {
			URI uri = new URI(uriString);
			return uri.isAbsolute();
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
