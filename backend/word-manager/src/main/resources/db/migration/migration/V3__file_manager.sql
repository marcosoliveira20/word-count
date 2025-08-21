-- V3__file_manager.sql

CREATE TABLE IF NOT EXISTS processed_object (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  object_key VARCHAR(512) NOT NULL,
  etag VARCHAR(64) NOT NULL,
  status VARCHAR(20) NOT NULL,
  message VARCHAR(1000),
  lines_total INT,
  processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_object_key_etag UNIQUE (object_key, etag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

CREATE TABLE IF NOT EXISTS processed_line (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     VARCHAR(64)  NOT NULL,
  word        VARCHAR(200) NOT NULL,
  source_file VARCHAR(512) NOT NULL,
  line_number BIGINT       NOT NULL,
  line_hash   CHAR(40)     NOT NULL, -- SHA-1 em hex
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  -- ✅ Dedupe sem extrapolar 3072 bytes:
  UNIQUE KEY uk_user_word_source_line (user_id, word(191), source_file(191), line_number),

  -- Índices auxiliares
  KEY idx_user_word (user_id, word(191)),
  KEY idx_source_file (source_file(191)),
  KEY idx_line_hash (line_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
