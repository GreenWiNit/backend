-- selling_status 컬럼 삭제
ALTER TABLE point_item
DROP
COLUMN IF EXISTS selling_status;

-- stock 컬럼 삭제
ALTER TABLE point_item
DROP
COLUMN IF EXISTS stock;
