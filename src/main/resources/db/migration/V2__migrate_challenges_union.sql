-- 개인/팀 챌린지를 생성일 순서로 통합
INSERT INTO challenges (
    code, name, point, content, image_url, type, display,
    participant_count, version, created_date, modified_date,
    created_by, last_modified_by
)
SELECT
    challenge_code,
    challenge_name,
    challenge_point::INTEGER,
        COALESCE(challenge_content, '내용 없음'),
    COALESCE(challenge_image, ''),
    'PERSONAL',
    display_status,
    participant_count,
    version,
    created_date,
    modified_date,
    created_by,
    last_modified_by
FROM personal_challenges

UNION ALL

SELECT
    challenge_code,
    challenge_name,
    challenge_point::INTEGER,
        COALESCE(challenge_content, '내용 없음'),
    COALESCE(challenge_image, ''),
    'TEAM',
    display_status,
    participant_count,
    version,
    created_date,
    modified_date,
    created_by,
    last_modified_by
FROM team_challenges

ORDER BY created_date, challenge_code;
