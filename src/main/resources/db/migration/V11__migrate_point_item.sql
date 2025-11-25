-- selling_status 컬럼 추가
ALTER TABLE greenwinitdb.point_item
    ADD COLUMN selling_status VARCHAR(20) NOT NULL DEFAULT 'EXCHANGEABLE';

-- stock 컬럼 추가
ALTER TABLE greenwinitdb.point_item
    ADD COLUMN stock INTEGER NOT NULL DEFAULT 0;

-- stock은 음수 불가
ALTER TABLE greenwinitdb.point_item
    ADD CONSTRAINT chk_stock_non_negative CHECK (stock >= 0);

ALTER TABLE greenwinitdb.point_item
    ALTER COLUMN selling_status DROP DEFAULT;

ALTER TABLE greenwinitdb.point_item
    ALTER COLUMN stock DROP DEFAULT;
