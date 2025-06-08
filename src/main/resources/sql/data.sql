-- 用户表 (user)
-- 密码 'password123' 和 'securepass' 建议在实际应用中进行哈希加密
INSERT INTO `user` (`id`, `username`, `password`, `role`, `create_time`, `update_time`) VALUES
                                                                                            (1, 'testuser', 'hashed_password_1', 'USER', '2024-05-01 10:00:00', '2024-05-01 10:00:00'),
                                                                                            (2, 'adminuser', 'hashed_password_2', 'ADMIN', '2024-05-01 10:05:00', '2024-05-01 10:05:00'),
                                                                                            (3, 'blogwriter', 'hashed_password_3', 'USER', '2024-05-02 11:00:00', '2024-05-02 11:00:00');

-- 文章表 (article)
INSERT INTO `article` (`id`, `title`, `content`, `author_id`,`author_name`, `create_time`, `update_time`) VALUES
                                                                                                (101, '我的第一篇文章', '这是我用Spring Boot和Android搭建的博客系统发布的第一篇文章。', 1, 'testuser' , '2024-05-05 14:30:00', '2024-05-05 14:30:00'),
                                                                                                (102, '技术博客：Retrofit使用指南', 'Retrofit是一个强大的Android网络请求库，本指南详细介绍了它的基本用法。', 3, 'newuser' '2024-05-06 09:00:00', '2024-05-06 09:00:00'),
                                                                                                (103, '旅行日记：探索新加坡美食', '新加坡是一个美食天堂，从街头小吃到高级餐厅，应有尽有。', 1, 'testuser' , '2024-05-07 16:00:00', '2024-05-07 16:00:00'),
                                                                                                (104, 'Spring Boot实战：文件上传与下载', '本文讲解了如何在Spring Boot中实现文件的安全上传和高效下载功能。', 3,'newuser' '2024-05-08 10:30:00', '2024-05-08 10:30:00');

-- 相册表 (photo)
-- 注意：file_path 假设为相对路径，您的服务器需要将其映射到实际存储路径
INSERT INTO `photo` (`id`, `name`, `description`, `file_path`, `file_type`, `file_size`, `author_id`, `author_name`, `create_time`, `update_time`) VALUES
                                                                                                                                        (201, '新加坡美食 - 辣椒螃蟹', '在东海岸海鲜中心品尝的美味辣椒螃蟹。', '1/chilli_crab.png', 'image/png', 120480, 1, 'testuser', '2024-05-08 11:00:00', '2024-05-08 11:00:00'),
                                                                                                                                        (202, '滨海湾花园夜景', '滨海湾花园的擎天大树在夜晚灯光璀璨。', '3/gardens_by_bay.png', 'image/png', 350120, 3, 'newuser', '2024-05-09 18:00:00', '2024-05-09 18:00:00'),
                                                                                                                                        (203, '我的开发环境', '我的笔记本电脑和外接显示器，编码进行时。', '1/dev_setup.png', 'image/png', 98765, 1, 'testuser', '2024-05-10 09:30:00', '2024-05-10 09:30:00');

-- 文章评论表 (article_comment)
INSERT INTO `article_comment` (`id`, `article_id`, `content`, `author_id`, `author_name`, `create_time`, `update_time`) VALUES
                                                                                                             (301, 101, '写得很好！期待更多文章。', 2, 'adminuser','2024-05-05 15:00:00', '2024-05-05 15:00:00'),
                                                                                                             (302, 102, 'Retrofit确实很好用，感谢分享。', 1, 'testuser','2024-05-06 10:15:00', '2024-05-06 10:15:00'),
                                                                                                             (303, 101, '支持博主！加油！', 3, 'newuser', '2024-05-07 08:30:00', '2024-05-07 08:30:00'),
                                                                                                             (304, 103, '新加坡美食真的名不虚传，下次我也要去！', 2,'adminuser', '2024-05-07 17:00:00', '2024-05-07 17:00:00');

-- 相册评论表 (photo_comment)
INSERT INTO `photo_comment` (`id`, `photo_id`, `content`, `author_id`, `author_name`,`create_time`, `update_time`) VALUES
                                                                                                         (401, 201, '看起来太美味了！口水直流。', 3, 'newuser','2024-05-08 12:00:00', '2024-05-08 12:00:00'),
                                                                                                         (402, 202, '夜景真漂亮，拍得真好！', 1, 'testuser', '2024-05-09 19:00:00', '2024-05-09 19:00:00'),
                                                                                                         (403, 201, '这是哪个餐厅的？求推荐！', 2, 'adminuser', '2024-05-08 13:00:00', '2024-05-08 13:00:00');