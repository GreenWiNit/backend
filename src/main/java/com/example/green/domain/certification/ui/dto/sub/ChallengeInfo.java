package com.example.green.domain.certification.ui.dto.sub;

import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = PersonalChallengeInfo.class, name = "P"),
	@JsonSubTypes.Type(value = TeamChallengeInfo.class, name = "T")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ChallengeInfo {
	private Long id;
	private String name;
	private String code;
	private String image;
	private Integer point;
	private String type;

	public static ChallengeInfo from(ChallengeSnapshot snapshot) {
		if (ChallengeSnapshot.PERSONAL_TYPE.equals(snapshot.getType())) {
			return PersonalChallengeInfo.from(snapshot);
		}
		return TeamChallengeInfo.from(snapshot);
	}
}