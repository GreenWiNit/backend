-- Add quantity column
ALTER TABLE greenwinitdb.order_point_item
    ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1;

-- Add total_price column
ALTER TABLE greenwinitdb.order_point_item
    ADD COLUMN total_price NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- Optionally remove default after migration (if desired)
ALTER TABLE greenwinitdb.order_point_item
    ALTER COLUMN quantity DROP DEFAULT;

ALTER TABLE greenwinitdb.order_point_item
    ALTER COLUMN total_price DROP DEFAULT;
