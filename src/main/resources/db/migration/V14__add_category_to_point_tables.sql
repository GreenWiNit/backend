-- point_item 테이블에 category 컬럼 추가
ALTER TABLE point_item
ADD COLUMN category VARCHAR(20);

-- 기존 point_item 데이터 → ITEM
UPDATE point_item
SET category = 'ITEM'
WHERE category IS NULL;

-- NOT NULL 제약 추가
ALTER TABLE point_item
ALTER COLUMN category SET NOT NULL;


-- point_products 테이블에 category 컬럼 추가
ALTER TABLE point_products
ADD COLUMN category VARCHAR(20);

-- 기존 point_products 데이터 → PRODUCT
UPDATE point_products
SET category = 'PRODUCT'
WHERE category IS NULL;

-- NOT NULL 제약 추가
ALTER TABLE point_products
ALTER COLUMN category SET NOT NULL;
