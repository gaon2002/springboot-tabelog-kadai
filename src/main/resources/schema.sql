-- houses テーブル生成とフィールド設定のコマンド

CREATE TABLE IF NOT EXISTS houses
(
    id           INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50)  NOT NULL,
    image_name   VARCHAR(255),
    description  VARCHAR(255) NOT NULL,
    price_min    INT          NOT NULL,
    price_max    INT          NOT NULL,
    capacity     INT          NOT NULL,
    postal_code  VARCHAR(50)  NOT NULL,
    address      VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50)  NOT NULL,
-- DEFAULT CURRENT_TIMESTAMP：時間の自動採番を設定
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- rolesテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS roles
(
    id   INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- usersテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS users
(
    id                      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_id                 INT          NOT NULL,
    name                    VARCHAR(50)  NOT NULL,
    furigana                VARCHAR(50)  NOT NULL,
    age                     INT          NOT NULL,
    birthday                DATE         NOT NULL,
    remember_token          VARCHAR(255),
    subscription_start_date DATE,
    subscription_end_date   DATE,
    postal_code             VARCHAR(50)  NOT NULL,
    address                 VARCHAR(255) NOT NULL,
    phone_number            VARCHAR(50)  NOT NULL,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    password                VARCHAR(255) NOT NULL,
    enabled                 BOOLEAN      NOT NULL,
    subscribe               INT          NOT NULL,
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);


-- verification_tokensテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS verification_tokens
(
    id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL UNIQUE,
    token      VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- reservationsテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS reservations
(
    id               INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    house_id         INT      NOT NULL,
    user_id          INT      NOT NULL,
    reserved_date    DATE     NOT NULL,
    reserved_time    TIME     NOT NULL,
    number_of_people INT      NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- レビュー用テーブルを作成
CREATE TABLE IF NOT EXISTS reviews
(
    id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    house_id   INT          NOT NULL,
    user_id    INT          NOT NULL,
    score      INT          NOT NULL,
    comment    VARCHAR(255) NOT NULL,
    display    INT          NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- お気に入り用テーブルを作成
CREATE TABLE IF NOT EXISTS favorites
(
    id         INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    house_id   INT      NOT NULL,
    user_id    INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- カテゴリーテーブルを作成
CREATE TABLE IF NOT EXISTS category
(
    id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category   VARCHAR(255) NOT NULL,
    image_name VARCHAR(255),
-- DEFAULT CURRENT_TIMESTAMP：時間の自動採番を設定
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- カテゴリー店舗テーブルを作成
CREATE TABLE IF NOT EXISTS houses_category
(
    id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    house_id    INT NOT NULL,
    category_id INT NOT NULL

);


-- password_reset_tokenテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS password_reset_token
(
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL UNIQUE,
    token       VARCHAR(255) NOT NULL,
    expiry_date DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP

);


-- companyテーブル生成とフィールド設定のコマンド
CREATE TABLE IF NOT EXISTS company
(
    id          		INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    company_name		VARCHAR(255) NOT NULL,
    principle			VARCHAR(255) NOT NULL,
    postal_code			VARCHAR(255) NOT NULL,
    address				VARCHAR(255) NOT NULL,
    establishment		VARCHAR(255) NOT NULL,
    capital_stock		VARCHAR(255) NOT NULL,
    business			VARCHAR(255) NOT NULL,
    number_of_member	VARCHAR(255) NOT NULL,
    phone_number		VARCHAR(255) NOT NULL,
    -- DEFAULT CURRENT_TIMESTAMP：時間の自動採番を設定
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    
);

