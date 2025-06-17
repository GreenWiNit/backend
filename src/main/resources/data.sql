-- 개발/테스트용 초기 회원 데이터
INSERT INTO MEMBER (nickname, profile_image_url, status, role, last_login_at, created_at, updated_at)
VALUES ('testNickname', 'testImageUrl', 'NORMAL', 'USER', NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE nickname = nickname; 