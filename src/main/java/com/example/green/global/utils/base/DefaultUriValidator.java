package com.example.green.global.utils.base;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Component;

import com.example.green.global.utils.UriValidator;

@Component
public class DefaultUriValidator implements UriValidator {

	@Override
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

}
