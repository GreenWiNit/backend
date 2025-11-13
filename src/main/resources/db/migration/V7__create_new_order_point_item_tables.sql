CREATE TABLE IF NOT EXISTS order_point_item
(
    point_item_order_id
    BIGSERIAL
    PRIMARY
    KEY,
    member_id
    BIGINT
    NOT
    NULL,
    member_key
    VARCHAR
(
    255
) NOT NULL,
    member_email VARCHAR
(
    255
) NOT NULL,
    point_item_id BIGINT NOT NULL,
    item_name VARCHAR
(
    255
) NOT NULL,
    item_code VARCHAR
(
    255
) NOT NULL,
    item_img_url VARCHAR
(
    255
) NOT NULL,
    item_price NUMERIC
(
    19,
    2
) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );
