-- Create the table
CREATE TABLE IF NOT EXISTS sessions (
                                        id VARCHAR(64) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );


DELIMITER //

CREATE TRIGGER session_set_created_date
    BEFORE INSERT ON sessions
    FOR EACH ROW
BEGIN
    SET NEW.created_at = CURRENT_TIMESTAMP();
END //

DELIMITER ;