CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(64) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TRIGGER session_set_created_date
    BEFORE INSERT ON sessions
    FOR EACH ROW
BEGIN
    SET NEW.created_at = CURRENT_TIMESTAMP;
END

DROP EVENT IF EXISTS delete_session;
CREATE EVENT delete_session
    ON SCHEDULE EVERY 1 MINUTE
    DO
DELETE FROM sessions WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 MINUTE);

