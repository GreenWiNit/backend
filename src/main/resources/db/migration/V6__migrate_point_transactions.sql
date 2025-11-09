-- 개인/팀 챌린지 매핑 정보 생성
WITH challenge_mapping AS (
    SELECT
        pc.id as old_id,
        pc.challenge_name,
        c.id as new_id
    FROM personal_challenges pc
             JOIN challenges c ON c.code = pc.challenge_code AND c.type = 'PERSONAL'

    UNION ALL

    SELECT
        tc.id as old_id,
        tc.challenge_name,
        c.id as new_id
    FROM team_challenges tc
             JOIN challenges c ON c.code = tc.challenge_code AND c.type = 'TEAM'
)
-- PointTransaction 업데이트
UPDATE point_transactions pt
SET target_id = cm.new_id
    FROM challenge_mapping cm
WHERE pt.target_type = 'CHALLENGE'
  AND pt.target_id = cm.old_id
  AND pt.description = cm.challenge_name || ' 완료'; -- "챌린지명 완료" (공백 1개)