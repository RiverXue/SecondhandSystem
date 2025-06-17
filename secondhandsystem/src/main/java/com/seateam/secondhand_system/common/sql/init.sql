CREATE DATABASE IF NOT EXISTS `secondhand`;
USE `secondhand`;

CREATE TABLE `user`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `username`    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名，唯一',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `avatar`      VARCHAR(255)                        DEFAULT NULL COMMENT '头像URL',
    `phone`       VARCHAR(20)                         DEFAULT NULL COMMENT '手机号',
    `role`        ENUM ('user', 'admin', 'merchants') DEFAULT 'user' COMMENT '用户角色（user-普通用户，admin-管理员，merchants-商家）',
    `create_time` DATETIME                            DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `goods`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `user_id`     BIGINT         NOT NULL COMMENT '发布用户ID',
    `title`       VARCHAR(100)   NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    `category`    VARCHAR(50) COMMENT '商品分类',
    `status`      TINYINT  DEFAULT 0 COMMENT '状态（0-在售，1-已售）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

CREATE TABLE `favorite`
(
    `id`       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `user_id`  BIGINT NOT NULL COMMENT '收藏用户ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='收藏表';

CREATE TABLE `message`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `goods_id`      BIGINT NOT NULL COMMENT '关联的商品ID',
    `user_id`       BIGINT NOT NULL COMMENT '留言用户ID',
    `content`       TEXT   NOT NULL COMMENT '留言内容',
    `reply_content` TEXT     DEFAULT NULL COMMENT '卖家回复内容（可空）',
    `reply_time`    DATETIME DEFAULT NULL COMMENT '卖家回复时间（可空）',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='留言表';

CREATE TABLE `order`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `buyer_id`    BIGINT         NOT NULL COMMENT '买家ID',
    `seller_id`   BIGINT         NOT NULL COMMENT '卖家ID',
    `goods_id`    BIGINT         NOT NULL COMMENT '商品ID',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '成交价格',
    `status`      TINYINT  DEFAULT 0 COMMENT '订单状态（0-未付款 1-已完成）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

CREATE TABLE `ai_chat`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `session_id`  VARCHAR(100) NOT NULL COMMENT '对话会话ID（多轮对话标识）',
    `message`     TEXT         NOT NULL COMMENT '用户输入内容',
    `response`    TEXT COMMENT 'AI回复内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='AI对话表';
