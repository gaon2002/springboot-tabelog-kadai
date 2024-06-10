-- housesテーブル生成とフィールド設定のコマンド

CREATE TABLE IF NOT EXISTS houses (
	id			 INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name		 VARCHAR(50)	NOT NULL,
	image_name	 VARCHAR(255),
	description	 VARCHAR(255)	NOT NULL,
	price_min	 INT			NOT NULL,
	price_max	 INT			NOT NULL,
	capacity 	 INT			NOT NULL,
	postal_code  VARCHAR(50)	NOT NULL,
	address 	 VARCHAR(255)	NOT NULL,
	phone_number VARCHAR(50)	NOT NULL,
-- DEFAULT CURRENT_TIMESTAMP：時間の自動採番を設定
	created_at	 DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at	 DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- rolesテーブル生成とフィールド設定のコマンド
 CREATE TABLE IF NOT EXISTS roles (
     id 		 INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
     name 		 VARCHAR(50) 	NOT NULL
 );
 
-- usersテーブル生成とフィールド設定のコマンド
 CREATE TABLE IF NOT EXISTS users (
     id				INT				NOT NULL AUTO_INCREMENT PRIMARY KEY,
     role_id		INT				NOT NULL,
     name			VARCHAR(50) 	NOT NULL,
     furigana		VARCHAR(50) 	NOT NULL,
     postal_code	VARCHAR(50) 	NOT NULL,
     address		VARCHAR(255)	NOT NULL,
     phone_number	VARCHAR(50) 	NOT NULL,
     email			VARCHAR(255) 	NOT NULL UNIQUE,
     password		VARCHAR(255) 	NOT NULL,    
     enabled		BOOLEAN 		NOT NULL,
     subscribe		INT				NOT NULL,
     created_at		DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at		DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,    
     FOREIGN KEY (role_id) REFERENCES roles (id)
 );
 
-- verification_tokensテーブル生成とフィールド設定のコマンド
 CREATE TABLE IF NOT EXISTS verification_tokens (
    id 				INT 			NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id 		INT 			NOT NULL UNIQUE,
    token 			VARCHAR(255) 	NOT NULL,        
    created_at 		DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at 		DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) 
 );
 
-- reservationsテーブル生成とフィールド設定のコマンド
 CREATE TABLE IF NOT EXISTS reservations (
     id 				INT 	NOT NULL AUTO_INCREMENT PRIMARY KEY,
     house_id 			INT 	NOT NULL,
     user_id 			INT 	NOT NULL,
     reserved_date 		DATE 	NOT NULL,
     reserved_time 		TIME 	NOT NULL,
     number_of_people 	INT 	NOT NULL,
     created_at 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (house_id) REFERENCES houses (id),
     FOREIGN KEY (user_id) REFERENCES users (id)
 );
 