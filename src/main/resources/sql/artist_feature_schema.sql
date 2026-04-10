CREATE DATABASE IF NOT EXISTS music_db;
USE music_db;

CREATE TABLE IF NOT EXISTS artist_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    artist_id BIGINT NOT NULL UNIQUE,
    stage_name VARCHAR(120),
    bio VARCHAR(2500),
    cover_image VARCHAR(255),
    social_links VARCHAR(1000),
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_artist_profiles_artist
        FOREIGN KEY (artist_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS artist_news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    artist_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    thumbnail VARCHAR(255),
    published TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_artist_news_artist
        FOREIGN KEY (artist_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_artist_news_artist_created_at
    ON artist_news(artist_id, created_at DESC);

CREATE INDEX idx_artist_news_artist_published_created_at
    ON artist_news(artist_id, published, created_at DESC);

-- Additional moderation/comment schema is in:
-- src/main/resources/sql/artist_comment_moderation_patch.sql
