CREATE TABLE IF NOT EXISTS plant_growth_item
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    member_id
    BIGINT
    NOT
    NULL,
    item_name
    VARCHAR
(
    255
) NOT NULL,
    item_img_url VARCHAR
(
    255
) NOT NULL,
    position_x DOUBLE PRECISION NOT NULL,
    position_y DOUBLE PRECISION NOT NULL,
    applicability BOOLEAN NOT NULL,
    version BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
