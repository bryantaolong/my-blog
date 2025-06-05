package com.example.myblog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user_favorites") // 映射到 user_favorites 表
public class UserFavorite implements Serializable {
    @TableId(type = IdType.AUTO) // ID 自动增长
    private Long id;
    private Long userId;     // 收藏者ID
    private String itemType; // 收藏项类型 (例如: "ARTICLE", "PHOTO")
    private Long itemId;     // 收藏项ID
    private Date createTime; // 收藏时间
}
