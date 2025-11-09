-- 새로운 통합 challenges 테이블
CREATE TABLE challenges
(
    id                BIGSERIAL PRIMARY KEY,
    code              VARCHAR(30)  NOT NULL UNIQUE,

    -- ChallengeInfo (Embeddable)
    name              VARCHAR(90)  NOT NULL,
    point             INTEGER      NOT NULL,

    -- ChallengeContent (Embeddable)
    content           TEXT         NOT NULL,
    image_url         VARCHAR(255) NOT NULL,

    -- Challenge 필드들
    type              VARCHAR(20)  NOT NULL,
    display           VARCHAR(20)  NOT NULL,
    participant_count INTEGER      NOT NULL DEFAULT 0,
    version           BIGINT,

    -- BaseEntity
    created_date      TIMESTAMP,
    modified_date     TIMESTAMP,
    created_by        VARCHAR(255),
    last_modified_by  VARCHAR(255),
    deleted           BOOLEAN               DEFAULT FALSE
);

-- 새로운 통합 challenge_participations 테이블
CREATE TABLE challenge_participations
(
    id               BIGSERIAL PRIMARY KEY,
    member_id        BIGINT  NOT NULL,
    cert_count       INTEGER NOT NULL DEFAULT 0,
    challenge_id     BIGINT  NOT NULL,

    -- BaseEntity
    created_date     TIMESTAMP,
    modified_date    TIMESTAMP,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted          BOOLEAN          DEFAULT FALSE,

    CONSTRAINT fk_challenge FOREIGN KEY (challenge_id)
        REFERENCES challenges (id) ON DELETE CASCADE
);
