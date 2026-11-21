ALTER TABLE product
ALTER COLUMN createdat SET NOT NULL;

ALTER TABLE product
ALTER COLUMN updatedat SET NOT NULL;

CREATE INDEX status_index
on product (status, price);