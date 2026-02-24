-- V1__initial_schema.sql (Postgres Version)

CREATE TABLE product_inventory (
                                   product_id VARCHAR(50) PRIMARY KEY,
                                   product_name VARCHAR(100) NOT NULL,
    -- PostgreSQL enforces this: quantity can NEVER be less than 0
                                   quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0)
);

CREATE TABLE product_reservation (
                                     id VARCHAR(50) PRIMARY KEY,
                                     product_id VARCHAR(50) NOT NULL,
                                     user_id VARCHAR(100) NOT NULL,
                                     status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                     expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     FOREIGN KEY (product_id) REFERENCES product_inventory(product_id),
    -- Ensure status is only one of these three values
                                     CONSTRAINT valid_status CHECK (status IN ('PENDING', 'COMPLETED', 'EXPIRED'))
);
