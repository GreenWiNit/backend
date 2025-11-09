-- teamChallengeId를 새로운 Challenge ID로 업데이트
UPDATE challenge_groups cg
SET team_challenge_id = c.id
    FROM team_challenges tc
JOIN challenges c ON c.code = tc.challenge_code AND c.type = 'TEAM'
WHERE cg.team_challenge_id = tc.id;