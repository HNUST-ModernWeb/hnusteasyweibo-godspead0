-- MySQL Database Schema for EasyWeibo
-- Database: hnusteasyweibo

CREATE DATABASE IF NOT EXISTS hnusteasyweibo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hnusteasyweibo;

-- User Table
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    `password` VARCHAR(255) NOT NULL COMMENT 'Password (BCrypt encrypted)',
    `nickname` VARCHAR(100) NOT NULL COMMENT 'Nickname',
    `avatar` VARCHAR(500) DEFAULT '' COMMENT 'Avatar URL',
    `bio` VARCHAR(500) DEFAULT '' COMMENT 'User bio',
    `following_count` INT DEFAULT 0 COMMENT 'Following count',
    `follower_count` INT DEFAULT 0 COMMENT 'Follower count',
    `post_count` INT DEFAULT 0 COMMENT 'Post count',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';

-- Post Table
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT 'Author user ID',
    `content` TEXT NOT NULL COMMENT 'Post content',
    `image_urls` VARCHAR(2000) DEFAULT '' COMMENT 'Image URLs (comma separated)',
    `like_count` INT DEFAULT 0 COMMENT 'Like count',
    `comment_count` INT DEFAULT 0 COMMENT 'Comment count',
    `repost_count` INT DEFAULT 0 COMMENT 'Repost count',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Post table';

-- Comment Table
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id` BIGINT NOT NULL COMMENT 'Post ID',
    `user_id` BIGINT NOT NULL COMMENT 'Commenter user ID',
    `content` TEXT NOT NULL COMMENT 'Comment content',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comment table';

-- Like Table
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `post_id` BIGINT NOT NULL COMMENT 'Post ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    INDEX `idx_post_id` (`post_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Like table';

-- Topic Table
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Topic name',
    `description` VARCHAR(500) DEFAULT '' COMMENT 'Topic description',
    `post_count` INT DEFAULT 0 COMMENT 'Post count using this topic',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Topic table';

-- Post-Topic Relation Table
DROP TABLE IF EXISTS `post_topic`;
CREATE TABLE `post_topic` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id` BIGINT NOT NULL COMMENT 'Post ID',
    `topic_id` BIGINT NOT NULL COMMENT 'Topic ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    UNIQUE KEY `uk_post_topic` (`post_id`, `topic_id`),
    INDEX `idx_topic_id` (`topic_id`),
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`topic_id`) REFERENCES `topic`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Post-Topic relation table';

-- Notification Table
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT 'Recipient user ID',
    `sender_id` BIGINT NOT NULL COMMENT 'Sender user ID',
    `type` VARCHAR(50) NOT NULL COMMENT 'Notification type: like, comment, follow, mention',
    `target_id` BIGINT NOT NULL COMMENT 'Target entity ID (post_id, comment_id, etc.)',
    `content` VARCHAR(500) DEFAULT '' COMMENT 'Notification content',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT 'Read status: 0=unread, 1=read',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Notification table';

-- Follow Table (User Following Relationship)
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `follower_id` BIGINT NOT NULL COMMENT 'Follower user ID',
    `following_id` BIGINT NOT NULL COMMENT 'Following user ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
    INDEX `idx_following_id` (`following_id`),
    FOREIGN KEY (`follower_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`following_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Follow relationship table';
