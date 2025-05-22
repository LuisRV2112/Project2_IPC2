ALTER TABLE ad_impressions 
ADD COLUMN user_id INT NULL,
ADD CONSTRAINT fk_impression_user FOREIGN KEY (user_id) REFERENCES users(id);