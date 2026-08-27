USE music_db;

-- Artist onboarding requests
CREATE TABLE IF NOT EXISTS artist_registration_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    portfolio_url VARCHAR(255),
    status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    admin_note TEXT,
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_artist_request_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_artist_request_admin FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

CREATE INDEX idx_artist_request_user_status ON artist_registration_requests(user_id, status, created_at);
CREATE INDEX idx_artist_request_status ON artist_registration_requests(status, created_at);

-- Song copyright / violation reports
CREATE TABLE IF NOT EXISTS song_violation_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    song_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    type ENUM('COPYRIGHT','PLAGIARISM','OTHER') NOT NULL DEFAULT 'COPYRIGHT',
    description TEXT NOT NULL,
    evidence_url VARCHAR(255),
    status ENUM('PENDING','RESOLVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note TEXT,
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_song_violation_song FOREIGN KEY (song_id) REFERENCES songs(id),
    CONSTRAINT fk_song_violation_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_song_violation_admin FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

CREATE INDEX idx_song_violation_reporter_created ON song_violation_reports(reporter_id, created_at);
CREATE INDEX idx_song_violation_status ON song_violation_reports(status, created_at);

-- Song comments and one-level replies
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    song_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    content TEXT NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_comment_song FOREIGN KEY (song_id) REFERENCES songs(id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comments(id)
);

CREATE INDEX idx_comment_song_root_created ON comments(song_id, parent_id, created_at);
CREATE INDEX idx_comment_parent_created ON comments(parent_id, created_at);
CREATE INDEX idx_comment_parent_deleted_created ON comments(parent_id, deleted, created_at);

-- Comment moderation reports
CREATE TABLE IF NOT EXISTS comment_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reason ENUM('SPAM','HARASSMENT','COPYRIGHT','OTHER') NOT NULL DEFAULT 'OTHER',
    detail TEXT NOT NULL,
    status ENUM('PENDING','RESOLVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note TEXT,
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_comment_report_comment FOREIGN KEY (comment_id) REFERENCES comments(id),
    CONSTRAINT fk_comment_report_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_comment_report_admin FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

CREATE INDEX idx_comment_report_status ON comment_reports(status, created_at);
CREATE INDEX idx_comment_report_comment_reporter_status ON comment_reports(comment_id, reporter_id, status);
