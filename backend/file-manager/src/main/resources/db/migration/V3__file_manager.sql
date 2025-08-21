-- Tabelas para idempotência/auditoria
CREATE TABLE IF NOT EXISTS processed_object (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    object_key VARCHAR(512) NOT NULL,
    etag VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message VARCHAR(1000),
    lines_total INT,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_object_key_etag UNIQUE (object_key, etag)
);

CREATE TABLE IF NOT EXISTS processed_line (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    word VARCHAR(200) NOT NULL,
    source_file VARCHAR(512) NOT NULL,
    line_number BIGINT NOT NULL,
    line_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_word_source_line UNIQUE (user_id, word, source_file, line_number)
);
