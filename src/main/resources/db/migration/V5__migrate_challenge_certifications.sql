-- 개인/팀 챌린지 매핑 정보 생성
WITH challenge_mapping AS (
    -- 개인 챌린지 매핑
    SELECT
        pc.id as old_id,
        'P' as old_type,
        c.id as new_id
    FROM personal_challenges pc
             JOIN challenges c ON c.code = pc.challenge_code AND c.type = 'PERSONAL'

    UNION ALL

    -- 팀 챌린지 매핑
    SELECT
        tc.id as old_id,
        'T' as old_type,
        c.id as new_id
    FROM team_challenges tc
             JOIN challenges c ON c.code = tc.challenge_code AND c.type = 'TEAM'
)
-- 한 번에 업데이트
UPDATE challenge_certifications cc
SET challenge_id = cm.new_id
    FROM challenge_mapping cm
WHERE cc.challenge_id = cm.old_id
  AND cc.type = cm.old_type;
