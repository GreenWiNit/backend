package com.example.green.domain.certification.ui.dto.sub;

import com.example.green.domain.certification.domain.ChallengeSnapshot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PersonalChallengeInfo extends ChallengeInfo {

	public static PersonalChallengeInfo from(ChallengeSnapshot snapshot) {
		PersonalChallengeInfo info = new PersonalChallengeInfo();
		info.setId(snapshot.getChallengeId());
		info.setName(snapshot.getChallengeName());
		info.setCode(snapshot.getChallengeCode());
		info.setImage(snapshot.getChallengeImage());
		info.setPoint(snapshot.getChallengePoint());
		info.setType(snapshot.getType());
		return info;
	}
}