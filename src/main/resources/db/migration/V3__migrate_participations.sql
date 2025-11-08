-- 개인/팀 챌린지 참여를 생성일 순서로 통합
INSERT INTO challenge_participations (member_id, cert_count, challenge_id,
                                      created_date, modified_date, created_by, last_modified_by)
SELECT pp.member_id,
       pp.cert_count,
       c.id,
       pp.created_date,
       pp.modified_date,
       pp.created_by,
       pp.last_modified_by
FROM personal_challenge_participations pp
         JOIN personal_challenges pc ON pp.personal_challenge_id = pc.id
         JOIN challenges c ON c.code = pc.challenge_code AND c.type = 'PERSONAL'

UNION ALL

SELECT tp.member_id,
       tp.cert_count,
       c.id,
       tp.created_date,
       tp.modified_date,
       tp.created_by,
       tp.last_modified_by
FROM team_challenge_participations tp
         JOIN team_challenges tc ON tp.team_challenge_id = tc.id
         JOIN challenges c ON c.code = tc.challenge_code AND c.type = 'TEAM'

ORDER BY created_date;