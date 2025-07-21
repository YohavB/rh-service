-- Create cars_relations table for blocking relationships
CREATE TABLE cars_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocking_car_id BIGINT NOT NULL,
    blocked_car_id BIGINT NOT NULL,
    FOREIGN KEY (blocking_car_id) REFERENCES cars(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_car_id) REFERENCES cars(id) ON DELETE CASCADE,
    CONSTRAINT unique_car_relation UNIQUE (blocking_car_id, blocked_car_id),
    CONSTRAINT no_self_blocking CHECK (blocking_car_id != blocked_car_id)
);

CREATE INDEX idx_cars_relations_blocking ON cars_relations(blocking_car_id);
CREATE INDEX idx_cars_relations_blocked ON cars_relations(blocked_car_id); 