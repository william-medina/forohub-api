CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO profiles (name) VALUES ('ADMIN'), ('MODERATOR'), ('INSTRUCTOR'), ('USER');