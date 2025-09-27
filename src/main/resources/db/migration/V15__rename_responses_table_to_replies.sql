-- Rename the table
ALTER TABLE responses RENAME TO replies;

-- Drop old foreign keys from replies
ALTER TABLE replies DROP FOREIGN KEY replies_ibfk_1;
ALTER TABLE replies DROP FOREIGN KEY replies_ibfk_2;

-- Recreate foreign keys in replies
ALTER TABLE replies
    ADD CONSTRAINT fk_replies_topic FOREIGN KEY (topic_id) REFERENCES topics(id),
    ADD CONSTRAINT fk_replies_user FOREIGN KEY (user_id) REFERENCES users(id);

-- Rename column in notifications from response_id to reply_id
ALTER TABLE notifications CHANGE COLUMN response_id reply_id BIGINT NULL;

-- Drop and recreate foreign key in notifications
ALTER TABLE notifications DROP FOREIGN KEY fk_notifications_response;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_reply FOREIGN KEY (reply_id) REFERENCES replies(id) ON DELETE SET NULL;

-- Expand ENUM to temporarily allow both values (RESPONSE and REPLY)
ALTER TABLE notifications
    MODIFY COLUMN type ENUM('TOPIC', 'RESPONSE', 'REPLY') NOT NULL;

-- Migrate existing records from RESPONSE to REPLY
UPDATE notifications
SET type = 'REPLY'
WHERE type = 'RESPONSE';

-- Restrict ENUM to final values (remove RESPONSE, keep only TOPIC and REPLY)
ALTER TABLE notifications
    MODIFY COLUMN type ENUM('TOPIC', 'REPLY') NOT NULL;
