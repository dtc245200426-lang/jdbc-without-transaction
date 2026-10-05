CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4;
USE demo;
CREATE TABLE IF NOT EXISTS users (
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(50) NOT NULL,
 email VARCHAR(50) NOT NULL,
 country VARCHAR(50) NOT NULL
) ENGINE=InnoDB;
DROP PROCEDURE IF EXISTS get_user_by_id;
DROP PROCEDURE IF EXISTS insert_user;
DELIMITER $$
CREATE PROCEDURE get_user_by_id(IN user_id INT)
BEGIN
 SELECT users.name, users.email, users.country FROM users WHERE users.id=user_id;
END$$
CREATE PROCEDURE insert_user(IN user_name VARCHAR(50),IN user_email VARCHAR(50),IN user_country VARCHAR(50))
BEGIN
 INSERT INTO users(name,email,country) VALUES(user_name,user_email,user_country);
END$$
DELIMITER ;

CREATE TABLE IF NOT EXISTS permission (
 id INT PRIMARY KEY, name VARCHAR(50)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS user_permission (
 user_id INT NOT NULL, permission_id INT NOT NULL,
 PRIMARY KEY(user_id, permission_id),
 FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 FOREIGN KEY(permission_id) REFERENCES permission(id)
) ENGINE=InnoDB;
INSERT INTO permission(id,name) VALUES (1,'add'),(2,'edit'),(3,'delete'),(4,'view')
 ON DUPLICATE KEY UPDATE name=VALUES(name);