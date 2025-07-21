-- Create users table
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    push_notification_token VARCHAR(500) NOT NULL,
    url_photo VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT unique_push_token UNIQUE (push_notification_token)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_push_token ON users(push_notification_token); 