-- V1: Base schema com N:N entre word e oxford_level

-- 1) Tabela de palavras
CREATE TABLE IF NOT EXISTS word (
                                    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name        VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_word_name        ON word (name);
CREATE INDEX idx_word_created_at  ON word (created_at);
CREATE INDEX idx_word_updated_at  ON word (updated_at);

-- 2) Oxford levels (A1..C2)
CREATE TABLE IF NOT EXISTS oxford_level (
                                            id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name  VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) N:N entre word e oxford_level
CREATE TABLE IF NOT EXISTS word_oxford_level (
                                                 word_id         BIGINT NOT NULL,
                                                 oxford_level_id BIGINT NOT NULL,
                                                 PRIMARY KEY (word_id, oxford_level_id),
    CONSTRAINT fk_wol_word
    FOREIGN KEY (word_id) REFERENCES word(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wol_level
    FOREIGN KEY (oxford_level_id) REFERENCES oxford_level(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wol_level ON word_oxford_level (oxford_level_id);

-- 4) Grammatical classes (noun, verb, etc.)
CREATE TABLE IF NOT EXISTS grammatical_class (
                                                 id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 name  VARCHAR(100) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) N:N entre word e grammatical_class
CREATE TABLE IF NOT EXISTS word_grammatical_class (
                                                      word_id               BIGINT NOT NULL,
                                                      grammatical_class_id  BIGINT NOT NULL,
                                                      PRIMARY KEY (word_id, grammatical_class_id),
    CONSTRAINT fk_wgc_word
    FOREIGN KEY (word_id) REFERENCES word(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wgc_class
    FOREIGN KEY (grammatical_class_id) REFERENCES grammatical_class(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wgc_class ON word_grammatical_class (grammatical_class_id);

-- 6) Shadow table de usuários
CREATE TABLE IF NOT EXISTS app_user (
                                        id             CHAR(36) PRIMARY KEY,    -- UUID do claim 'sub'
    username       VARCHAR(150) NOT NULL,
    email          VARCHAR(255),
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) Log de uso de palavras
CREATE TABLE IF NOT EXISTS word_usage_log (
                                              id        BIGINT NOT NULL AUTO_INCREMENT,
                                              word_id   BIGINT NOT NULL,
                                              user_id   CHAR(36) NOT NULL,
    used_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_wul_word
    FOREIGN KEY (word_id) REFERENCES word(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wul_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON UPDATE CASCADE ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wul_user_word   ON word_usage_log (user_id, word_id);
CREATE INDEX idx_wul_word_time   ON word_usage_log (word_id, used_at);
CREATE INDEX idx_wul_user_time   ON word_usage_log (user_id, used_at);

-- 8) Views para agregados
CREATE VIEW v_word_usage_stats AS
SELECT
    l.user_id,
    l.word_id,
    COUNT(*)       AS used_times,
    MIN(l.used_at) AS first_use,
    MAX(l.used_at) AS last_use
FROM word_usage_log l
GROUP BY l.user_id, l.word_id;

CREATE VIEW v_word_details_with_usage AS
SELECT
    w.id                         AS word_id,
    w.name                       AS name,
    s.used_times,
    s.first_use,
    s.last_use,
    w.created_at,
    w.updated_at
FROM word w
         LEFT JOIN v_word_usage_stats s
                   ON s.word_id = w.id;
