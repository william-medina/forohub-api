ALTER TABLE topics
ADD COLUMN course_id BIGINT,
ADD CONSTRAINT fk_course
FOREIGN KEY (course_id) REFERENCES courses(id);