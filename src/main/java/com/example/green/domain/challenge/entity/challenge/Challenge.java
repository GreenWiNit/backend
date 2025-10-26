package com.example.green.domain.challenge.entity.challenge;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 30, nullable = false, unique = true)
	private String code;

	@Embedded
	private ChallengeInfo info;

	@Embedded
	private ChallengeContent content;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeType type;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeDisplay display;

	@Column(nullable = false)
	private Integer participantCount;

	@OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Participation> participations = new ArrayList<>();

	@Version
	private Long version;

	private Challenge(String code, ChallengeInfo info, ChallengeContent content, ChallengeType type) {
		validate(code, info, content, type);    // 개발 레벨에서(개발자의 실수 등을) 검증하는거라 검증할 필요는 없음
		this.code = code;
		this.info = info;
		this.content = content;
		this.type = type;
		this.display = ChallengeDisplay.VISIBLE;
		this.participantCount = 0;
	}

	private static void validate(String code, ChallengeInfo info, ChallengeContent content, ChallengeType type) {
		Objects.requireNonNull(code, CHALLENGE_CODE_NON_NULL);
		Objects.requireNonNull(info, CHALLENGE_INFO_NON_NULL);
		Objects.requireNonNull(content, CHALLENGE_CONTENT_NON_NULL);
		Objects.requireNonNull(type, CHALLENGE_TYPE_NON_NULL);
	}

	public static Challenge of(String code, ChallengeInfo info, ChallengeContent content, ChallengeType type) {
		return new Challenge(code, info, content, type);
	}

	public void show() {
		this.display = ChallengeDisplay.VISIBLE;
	}

	public void hide() {
		this.display = ChallengeDisplay.HIDDEN;
	}

	public void updateInfo(ChallengeInfo info) {
		Objects.requireNonNull(info, CHALLENGE_INFO_NON_NULL);
		this.info = info;
	}

	public void updateContent(ChallengeContent content) {
		Objects.requireNonNull(content, CHALLENGE_CONTENT_NON_NULL);
		this.content = content;
	}

	public void participate(Long memberId) {
		validateParticipation(memberId);
		Participation participation = Participation.create(this, memberId);
		participations.add(participation);
		this.participantCount++;
	}

	public String getImageUrl() {
		return content.getImageUrl();
	}

	private void validateParticipation(Long memberId) {
		if (isAlreadyParticipated(memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING);
		}
		if (display == ChallengeDisplay.HIDDEN) {
			throw new ChallengeException(ChallengeExceptionMessage.INACTIVE_CHALLENGE);
		}
	}

	private boolean isAlreadyParticipated(Long memberId) {
		return participations.stream()
			.anyMatch(p -> p.isParticipated(memberId));
	}
}
