USE music_db;

CREATE TABLE IF NOT EXISTS popup_ads (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image VARCHAR(500),
    target_url VARCHAR(500),
    active TINYINT(1) NOT NULL DEFAULT 1,
    start_at DATETIME NULL,
    end_at DATETIME NULL,
    created_at DATETIME NULL
);

CREATE INDEX idx_popup_ads_active_time ON popup_ads(active, start_at, end_at, created_at);

ALTER TABLE comments
    ADD COLUMN IF NOT EXISTS ai_moderation_reason VARCHAR(500) NULL,
    ADD COLUMN IF NOT EXISTS ai_moderated_at DATETIME NULL;
