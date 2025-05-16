CREATE TABLE customer (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE complaint (
                           id BIGSERIAL PRIMARY KEY,
                           product_id BIGINT NOT NULL,
                           content TEXT NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           customer_id BIGINT NOT NULL,
                           country VARCHAR(100) NOT NULL,
                           submission_count INTEGER NOT NULL DEFAULT 1,
                           CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
                           CONSTRAINT uq_product_customer UNIQUE (product_id, customer_id)
);