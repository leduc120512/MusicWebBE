USE music_db;

CREATE INDEX idx_song_violation_reports_song_status
    ON song_violation_reports(song_id, status);

CREATE INDEX idx_song_violation_reports_reporter_status
    ON song_violation_reports(reporter_id, status);
