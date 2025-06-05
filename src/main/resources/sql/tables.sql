-- 用户表
CREATE TABLE `user`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(50)  NOT NULL COMMENT '用户名',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `role`        varchar(20)  NOT NULL DEFAULT 'USER' COMMENT '角色',
    `bio`         VARCHAR(500)          DEFAULT NULL, -- 或者可以设置一个默认的空字符串 ''
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 文章表
CREATE TABLE `article`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title`       varchar(100) NOT NULL COMMENT '标题',
    `content`     text         NOT NULL COMMENT '内容',
    `author_id`   bigint       NOT NULL COMMENT '作者ID',
    `author_name` VARCHAR(255) NULL COMMENT '作者名称',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文章表';

-- 相册表
CREATE TABLE `photo`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) NOT NULL COMMENT '图片名称',
    `description` varchar(255)          DEFAULT NULL COMMENT '图片描述',
    `file_path`   varchar(255) NOT NULL COMMENT '服务器存储路径',
    `file_type`   varchar(50)  NOT NULL COMMENT '文件类型',
    `file_size`   bigint       NOT NULL COMMENT '文件大小(字节)',
    `author_id`   bigint       NOT NULL COMMENT '上传用户ID',
    `author_name` VARCHAR(255) NULL COMMENT '上传用户名',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 文章评论表
CREATE TABLE `article_comment`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id`  bigint   NOT NULL COMMENT '文章ID',
    `content`     text     NOT NULL COMMENT '评论内容',
    `author_id`   bigint   NOT NULL COMMENT '评论者ID',
    `author_name` VARCHAR(255)      DEFAULT '未知用户',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文章评论表';

-- 相册评论表
CREATE TABLE `photo_comment`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `photo_id`    bigint   NOT NULL COMMENT '图片ID',
    `content`     text     NOT NULL COMMENT '评论内容',
    `author_id`   bigint   NOT NULL COMMENT '评论者ID',
    `author_name` VARCHAR(255)      DEFAULT '未知用户',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_photo_id` (`photo_id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='相册评论表';

CREATE TABLE user_follows (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              follower_id BIGINT NOT NULL,
                              following_id BIGINT NOT NULL,
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE (follower_id, following_id), -- 确保没有重复的关注关系
                              FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE, -- 关注者ID外键，级联删除
                              FOREIGN KEY (following_id) REFERENCES user(id) ON DELETE CASCADE  -- 被关注者ID外键，级联删除
);

CREATE TABLE user_favorites (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                item_type VARCHAR(50) NOT NULL, -- 'ARTICLE' 或 'PHOTO'
                                item_id BIGINT NOT NULL,
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                UNIQUE (user_id, item_type, item_id), -- 确保同一用户不能重复收藏同一类型同一ID的项
                                FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
