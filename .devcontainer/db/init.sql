DROP DATABASE IF EXISTS `my-books-db`;
CREATE DATABASE `my-books-db`;

USE `my-books-db`;

DROP TABLE IF EXISTS `books`;
DROP TABLE IF EXISTS `genres`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;

CREATE TABLE `books` (
  `id` VARCHAR(255) NOT NULL,
  `title` VARCHAR(255) NOT NULL DEFAULT '',
  `description` TEXT NOT NULL,
  `genre_ids` VARCHAR(255) NOT NULL DEFAULT '',
  `authors` VARCHAR(255) NOT NULL DEFAULT '',
  `publisher` VARCHAR(255) NOT NULL DEFAULT '',
  `published_date` DATE NOT NULL,
  `price` INT(11) NOT NULL DEFAULT 0,
  `page_count` INT(11) NOT NULL DEFAULT 0,
  `isbn` VARCHAR(255) NOT NULL DEFAULT '',
  `image_url` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
);

CREATE TABLE `genres` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `description` VARCHAR(255) NOT NULL DEFAULT '',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=10;


CREATE TABLE `users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL DEFAULT '',
  `password` VARCHAR(255) NOT NULL DEFAULT '',
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);

CREATE TABLE `roles` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `description` VARCHAR(255) NOT NULL DEFAULT '',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

CREATE TABLE `user_roles` (
  `user_id` INT(11) NOT NULL,
  `role_id` INT(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE
);

-- データのロード
LOAD DATA INFILE '/docker-entrypoint-initdb.d/data.csv'
INTO TABLE books
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(`id`, `title`, `description`, `genre_ids`, `authors`, `publisher`, `published_date`, `price`, `page_count`, `isbn`, `image_url`);

INSERT INTO `genres` (`name`, `description`) VALUES
('ミステリー', '謎解きや推理をテーマにした作品'),
('サスペンス', '緊張感や驚きを伴う作品'),
('ロマンス', '恋愛をテーマにした作品'),
('ファンタジー', '魔法や異世界を舞台にした作品'),
('SF', '科学技術や未来をテーマにした作品'),
('ホラー', '恐怖をテーマにした作品'),
('歴史', '歴史的な出来事や人物をテーマにした作品'),
('絵本', '子供向けのイラストが多い本'),
('教科書', '教育機関で使用される教材'),
('専門書', '特定の分野に特化した書籍'),
('研究書', '学術的な研究をまとめた書籍'),
('環境', '自然や環境問題をテーマにした作品'),
('冒険', '冒険や探検をテーマにした作品'),
('図鑑', '特定のテーマに関する情報を集めた書籍'),
('音楽', '音楽に関する書籍'),
('ドラマ', '人間関係や感情を描いた作品'),
('教育', '教育に関する書籍');

INSERT INTO `users` (`name`, `email`, `password`, `avatar_url`) VALUES
('Julia', 'julia@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar02.png'),
('Steve', 'steve@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar07.png');

INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_ADMIN', '管理者権限'),
('ROLE_USER', 'ユーザー権限');

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 2),
(2, 2);