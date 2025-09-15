-- 3) FK 추가 (users.userid 이름은 실제 컬럼명에 맞춰 조정)
ALTER TABLE checklist_items
    ADD CONSTRAINT fk_ci_user
        FOREIGN KEY (user_id) REFERENCES users(userid);

ALTER TABLE record_photos
    ADD CONSTRAINT uq_record_photos_items UNIQUE (items_id);

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_records;

ALTER TABLE checklist_items
    ADD CONSTRAINT user_id
        FOREIGN KEY (user_id) REFERENCES users(userid);

ALTER TABLE checklist_items
    DROP INDEX user_id;

ALTER TABLE record_photos
    ADD CONSTRAINT fk_photos_items
        FOREIGN KEY (items_id) REFERENCES checklist_items(check_id)
            ON DELETE CASCADE;

ALTER TABLE checklist_items
    MODIFY COLUMN check_id BIGINT NOT NULL AUTO_INCREMENT;

CREATE INDEX idx_ci_user_id ON checklist_items(user_id);

ALTER TABLE checklist_items
    ADD CONSTRAINT checklist_items_users_userid_fk
        FOREIGN KEY (user_id) REFERENCES users (userid)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

ALTER TABLE checklist_items
    ADD COLUMN user_id BIGINT NOT NULL AFTER check_id;

TRUNCATE TABLE checklist_items;