package com.example.green.infra.mail;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class ImapConfig {

	@Bean
	public Properties imapConnectionProperties(MailProperties mailProperties) {
		Properties props = new Properties();
		props.put("mail.store.protocol", "imaps");

		Map<String, String> mailProps = mailProperties.getProperties();
		props.put("mail.imaps.host", mailProps.get("mail.imap.host"));
		props.put("mail.imaps.port", mailProps.get("mail.imap.port"));
		props.put("mail.imaps.ssl.enable", mailProps.get("mail.imap.ssl.enable"));
		props.put("mail.imaps.connectiontimeout", mailProps.get("mail.imap.connectiontimeout"));
		props.put("mail.imaps.timeout", mailProps.get("mail.imap.timeout"));

		return props;
	}

	@Bean
	public ImapCredentials imapCredentials(MailProperties mailProperties) {
		return new ImapCredentials(mailProperties.getUsername(), mailProperties.getPassword());
	}
}
