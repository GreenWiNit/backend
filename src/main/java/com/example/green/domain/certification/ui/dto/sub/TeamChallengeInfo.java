package com.example.green.domain.certification.ui.dto.sub;

import com.example.green.domain.certification.domain.ChallengeSnapshot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TeamChallengeInfo extends ChallengeInfo {
	private String groupCode;

	public static TeamChallengeInfo from(ChallengeSnapshot snapshot) {
		TeamChallengeInfo info = new TeamChallengeInfo();
		info.setId(snapshot.getChallengeId());
		info.setName(snapshot.getChallengeName());
		info.setCode(snapshot.getChallengeCode());
		info.setImage(snapshot.getChallengeImage());
		info.setPoint(snapshot.getChallengePoint());
		info.setType(snapshot.getType());
		info.setGroupCode(snapshot.getGroupCode());
		return info;
	}
}