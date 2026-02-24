
CREATE INDEX idx_reservation_lookup ON product_reservation (product_id, status, expires_at);

-- Seed initial data
INSERT INTO product_inventory (product_id, product_name, quantity)
VALUES ('tshirt_001', 'Cool T-Shirt', 10),
       ('hat_001', 'Fancy Hat', 5);