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
     id							INT				NOT NULL AUTO_INCREMENT PRIMARY KEY,
     role_id					INT				NOT NULL,
     name						VARCHAR(50) 	NOT NULL,
     furigana					VARCHAR(50) 	NOT NULL,
     age						INT 			NOT NULL,
     birthday 					DATE 			NOT NULL,
     remember_token 			VARCHAR(255),
     subscription_start_date 	DATE,
     subscription_end_date 		DATE,
     postal_code				VARCHAR(50) 	NOT NULL,
     address					VARCHAR(255)	NOT NULL,
     phone_number				VARCHAR(50) 	NOT NULL,
     email						VARCHAR(255) 	NOT NULL UNIQUE,
     password					VARCHAR(255) 	NOT NULL,    
     enabled					BOOLEAN 		NOT NULL,
     subscribe					INT				NOT NULL,
     created_at					DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at					DATETIME 		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,    
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
 
  -- レビュー用テーブルを作成
  CREATE TABLE IF NOT EXISTS reviews (
     id			 INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
     house_id	 INT			NOT NULL,
     user_id	 INT			NOT NULL,
     score		 INT			NOT NULL,
     comment	 VARCHAR(255)	NOT NULL,
     display	 INT			NOT NULL,
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (house_id) REFERENCES houses (id),
     FOREIGN KEY (user_id) REFERENCES users (id)
 );
 
    -- お気に入り用テーブルを作成
  CREATE TABLE IF NOT EXISTS favorites (
     id			 INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
     house_id	 INT			NOT NULL,
     user_id	 INT			NOT NULL,
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     FOREIGN KEY (house_id) REFERENCES houses (id),
     FOREIGN KEY (user_id) REFERENCES users (id)
 );
 
     -- カテゴリー店舗テーブルを作成
  CREATE TABLE IF NOT EXISTS houses_category (
     id			 	INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
     house_id	 	INT			NOT NULL,
     category_id	INT			NOT NULL,
     FOREIGN KEY (house_id) 	REFERENCES houses (id),
     FOREIGN KEY (category_id) REFERENCES category (id)
 );
 

 
      -- カテゴリーテーブルを作成
   CREATE TABLE IF NOT EXISTS category (
     id			 INT			NOT NULL AUTO_INCREMENT PRIMARY KEY,
     category	 VARCHAR(255)	NOT NULL,
     image_name	 VARCHAR(255),
-- DEFAULT CURRENT_TIMESTAMP：時間の自動採番を設定
	 created_at	 DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP,
	 updated_at	 DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 );
 
 -- 外部キー制約の削除
ALTER TABLE houses_category DROP FOREIGN KEY houses_category_ibfk_2;

-- 外部キー制約の追加（カスケード削除付き）
ALTER TABLE houses_category
ADD CONSTRAINT houses_category_ibfk_2
FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;