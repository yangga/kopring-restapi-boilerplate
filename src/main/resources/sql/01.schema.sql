CREATE TABLE IF NOT EXISTS users (
     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     email VARCHAR(1024) NOT NULL,
     password VARCHAR(1024) NOT NULL,
     username VARCHAR(1024) NOT NULL,
     CONSTRAINT udx_email UNIQUE (email)
);