-- 创建数据库
CREATE DATABASE IF NOT EXISTS community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE community;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    nickname    VARCHAR(50)  COMMENT '昵称',
    email       VARCHAR(100) COMMENT '邮箱',
    avatar      VARCHAR(255) COMMENT '头像URL',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, ADMIN-管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常, 0-禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删, 1-已删',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 文章表
CREATE TABLE IF NOT EXISTS article (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '作者ID',
    title       VARCHAR(100) NOT NULL COMMENT '标题',
    summary     VARCHAR(300) COMMENT '摘要',
    content     MEDIUMTEXT   NOT NULL COMMENT '正文',
    category    VARCHAR(50)  COMMENT '分类',
    cover_url   VARCHAR(255) COMMENT '封面图',
    view_count  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    like_count  INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-已发布, 0-草稿',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_category (category)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章表';

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    article_id  BIGINT     NOT NULL COMMENT '文章ID',
    user_id     BIGINT     NOT NULL COMMENT '评论用户ID',
    content     VARCHAR(500) NOT NULL COMMENT '评论内容',
    deleted     TINYINT    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_article_id (article_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '评论表';

-- 点赞记录表（防止重复点赞）
CREATE TABLE IF NOT EXISTS article_like (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    article_id  BIGINT NOT NULL COMMENT '文章ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_article_user (article_id, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章点赞表';

-- 说明：注册普通用户后，如需管理员权限，在数据库执行：
-- UPDATE user SET role='ADMIN' WHERE username='你的用户名';
