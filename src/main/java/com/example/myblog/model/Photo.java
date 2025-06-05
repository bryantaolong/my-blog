package com.example.myblog.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Photo {
    private Long id;
    private String name;
    private String description;
    private String filePath;  // 改为存储服务器相对路径
    private String fileType;  // 文件类型
    private Long fileSize;    // 文件大小(字节)
    private String authorId;
    private String authorName;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}