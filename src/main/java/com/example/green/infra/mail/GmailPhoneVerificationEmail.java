package com.example.green.infra.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationEmail;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GmailPhoneVerificationEmail implements PhoneVerificationEmail {

	private final Properties imapConnectionProperties;
	private final ImapCredentials imapCredentials;

	@Override
	public String getServerEmail() {
		return imapCredentials.userName();
	}

	@Override
	public Optional<String> extractTokenByPhoneNumber(PhoneNumber phoneNumber, LocalDateTime since) {
		try {
			Store store = connectToEmailStore();
			Folder inbox = openInboxFolder(store);
			Optional<String> result = Optional.ofNullable(searchTokenInEmails(inbox, phoneNumber, since));
			closeConnections(inbox, store);
			return result;
		} catch (Exception e) {
			log.error("메일 확인 중 오류 발생: {}", e.getMessage());
			return Optional.empty();
		}
	}

	private Store connectToEmailStore() throws MessagingException {
		Session session = Session.getDefaultInstance(imapConnectionProperties);
		Store store = session.getStore("imaps");
		store.connect(imapCredentials.userName(), imapCredentials.password());
		return store;
	}

	private Folder openInboxFolder(Store store) throws MessagingException {
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);
		return inbox;
	}

	private String searchTokenInEmails(Folder inbox, PhoneNumber phoneNumber, LocalDateTime since)
		throws MessagingException {
		Date sinceDate = Date.from(since.atZone(ZoneId.systemDefault()).toInstant());
		SearchTerm timeTerm = new ReceivedDateTerm(ComparisonTerm.GE, sinceDate);
		Message[] messages = inbox.search(timeTerm);

		log.info("검색된 메일 개수: {}, 전화번호: {}", messages.length, phoneNumber.getNumber());

		Arrays.sort(messages, (a, b) -> {
			try {
				Date dateA = a.getReceivedDate();
				Date dateB = b.getReceivedDate();

				if (dateA == null && dateB == null)
					return 0;
				if (dateA == null)
					return 1;
				if (dateB == null)
					return -1;

				return dateB.compareTo(dateA);
			} catch (MessagingException e) {
				log.warn("메일 날짜 비교 오류: {}", e.getMessage());
				return 0;
			}
		});

		for (Message message : messages) {
			try {
				log.debug("메일 확인 - 발신자: {}, 수신시간: {}",
					getFromAddress(message), getReceivedTime(message));
			} catch (Exception e) {
				log.debug("메일 정보 출력 실패");
			}

			if (isFromPhoneNumber(message, phoneNumber)) {
				// 본문 + 첨부파일에서만 토큰 추출
				String token = extractTokenFromMessageContent(message);
				if (token != null) {
					log.info("전화번호: {}, 토큰: {}, 수신시간: {}",
						phoneNumber.getNumber(), token, getReceivedTime(message));
					return token;
				}
			}
		}

		log.warn("토큰을 찾을 수 없음: 전화번호 {}", phoneNumber.getNumber());
		return null;
	}

	private boolean isFromPhoneNumber(Message message, PhoneNumber phoneNumber) {
		try {
			Address[] from = message.getFrom();
			if (from == null || from.length == 0)
				return false;

			String fromAddress = from[0].toString();
			String cleanPhone = phoneNumber.getNumber().replaceAll("-", "");
			return fromAddress.contains(cleanPhone + "@");

		} catch (MessagingException e) {
			return false;
		}
	}

	private String extractTokenFromMessageContent(Message message) {
		try {
			// LG U+: 단순 텍스트 본문
			if (message.isMimeType("text/plain")) {
				String content = (String)message.getContent();
				return extractToken(content);
			}

			// KT: multipart (본문 + 첨부파일)
			if (message.isMimeType("multipart/*")) {
				return extractFromMultipart(message);
			}

			// todo: SKT 처리방식 아직 모름
			return null;
		} catch (Exception e) {
			log.warn("메시지 내용 추출 실패: {}", e.getMessage());
			return null;
		}
	}

	private String extractFromMultipart(Message message) throws MessagingException, IOException {
		Multipart multipart = (Multipart)message.getContent();

		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);

			// 1. 텍스트 본문에서 토큰 찾기
			if (bodyPart.isMimeType("text/plain")) {
				String content = (String)bodyPart.getContent();
				String token = extractToken(content);
				if (token != null) {
					log.debug("텍스트 본문에서 토큰 발견: {}", token);
					return token;
				}
			}

			// 2. 텍스트 파일 첨부에서 토큰 찾기 (아이폰 KT)
			if (isTextFile(bodyPart)) {
				InputStream inputStream = bodyPart.getInputStream();
				String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				String token = extractToken(content);
				if (token != null) {
					log.debug("첨부파일에서 토큰 발견: {}", token);
					return token;
				}
			}
		}
		return null;
	}

	private boolean isTextFile(BodyPart bodyPart) throws MessagingException {
		String fileName = bodyPart.getFileName();
		return fileName != null && fileName.toLowerCase().endsWith(".txt");
	}

	private String extractToken(String content) {
		if (content == null) {
			return null;
		}

		Pattern pattern = Pattern.compile("[A-Za-z0-9]{16}");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	private String getFromAddress(Message message) {
		try {
			Address[] from = message.getFrom();
			if (from != null && from.length > 0) {
				return from[0].toString();
			}
			return "알 수 없음";
		} catch (MessagingException e) {
			return "오류";
		}
	}

	private String getReceivedTime(Message message) {
		try {
			Date receivedDate = message.getReceivedDate();
			if (receivedDate == null) {
				return "알 수 없음";
			}
			return receivedDate.toString();
		} catch (MessagingException e) {
			return "오류";
		}
	}

	private void closeConnections(Folder inbox, Store store) {
		try {
			if (inbox != null && inbox.isOpen()) {
				inbox.close(false);
			}
		} catch (MessagingException e) {
			log.warn("Folder 종료 오류: {}", e.getMessage());
		}

		try {
			if (store != null && store.isConnected()) {
				store.close();
			}
		} catch (MessagingException e) {
			log.warn("Store 종료 오류: {}", e.getMessage());
		}
	}
}