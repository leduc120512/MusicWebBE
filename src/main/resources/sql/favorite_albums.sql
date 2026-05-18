USE music_db;

CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL,
    cover_image VARCHAR(255) NULL,
    is_public TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_playlists_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS playlist_songs (
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    PRIMARY KEY (playlist_id, song_id),
    CONSTRAINT fk_playlist_songs_playlist
        FOREIGN KEY (playlist_id) REFERENCES playlists(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_playlist_songs_song
        FOREIGN KEY (song_id) REFERENCES songs(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_playlists_user_id ON playlists(user_id);
CREATE INDEX idx_playlist_songs_song_id ON playlist_songs(song_id);
