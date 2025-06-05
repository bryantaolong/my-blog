package com.example.myblog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user_follows") // 映射到 user_follows 表
public class UserFollow implements Serializable {
    @TableId(type = IdType.AUTO) // ID 自动增长
    private Long id;
    private Long followerId; // 关注者ID
    private Long followingId; // 被关注者ID
    private Date createTime;  // 关注时间
}
