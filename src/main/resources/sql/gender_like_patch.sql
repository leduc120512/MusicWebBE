USE music_db;

-- Add gender for user profile (safe on MySQL 8+)
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS gender ENUM('MALE','FEMALE','OTHER') DEFAULT 'OTHER' AFTER avatar;

-- Keep like records unique per user-song pair
SET @idx_likes_user_song_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'likes'
      AND index_name = 'idx_likes_user_song'
);
SET @sql := IF(@idx_likes_user_song_exists = 0,
    'CREATE UNIQUE INDEX idx_likes_user_song ON likes(user_id, song_id)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Helpful indexes for query performance
SET @idx_likes_song_id_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'likes'
      AND index_name = 'idx_likes_song_id'
);
SET @sql := IF(@idx_likes_song_id_exists = 0,
    'CREATE INDEX idx_likes_song_id ON likes(song_id)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_likes_user_created_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'likes'
      AND index_name = 'idx_likes_user_id_created_at'
);
SET @sql := IF(@idx_likes_user_created_exists = 0,
    'CREATE INDEX idx_likes_user_id_created_at ON likes(user_id, created_at)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


