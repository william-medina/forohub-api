CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL
);

INSERT INTO courses (name, category) VALUES
    ('Introducción a Java y Programación Orientada a Objetos', 'Java'),
    ('Desarrollo de Aplicaciones Web con Spring Boot', 'Spring Boot'),
    ('Spring Boot: Creación de API RESTful', 'Spring Boot'),
    ('Desarrollo Frontend con React', 'Frontend'),
    ('React + TypeScript: Arquitectura y Optimización de Aplicaciones', 'Frontend');