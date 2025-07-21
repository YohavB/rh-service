-- Create users_cars junction table
CREATE TABLE users_cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_car UNIQUE (user_id, car_id)
);

CREATE INDEX idx_users_cars_user_id ON users_cars(user_id);
CREATE INDEX idx_users_cars_car_id ON users_cars(car_id); 