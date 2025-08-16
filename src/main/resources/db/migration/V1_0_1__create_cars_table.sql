-- Create cars table
CREATE TABLE cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    brand VARCHAR(100) DEFAULT 'UNKNOWN',
    model VARCHAR(255) NOT NULL,
    color VARCHAR(100) DEFAULT 'UNKNOWN',
    year INT NOT NULL,
    car_license_expire_date TIMESTAMP NULL,
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_plate_number UNIQUE (plate_number)
);

CREATE INDEX idx_cars_plate_number ON cars(plate_number);