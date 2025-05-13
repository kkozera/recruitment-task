CREATE TABLE complaint (
                           id BIGSERIAL PRIMARY KEY,
                           product_id BIGINT NOT NULL,
                           content TEXT NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           submitted_by VARCHAR(255) NOT NULL,
                           country VARCHAR(100) NOT NULL,
                           submission_count INTEGER NOT NULL DEFAULT 1,
                           CONSTRAINT unique_product_and_submitter UNIQUE (product_id, submitted_by)
);