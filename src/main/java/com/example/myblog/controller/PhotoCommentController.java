package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.PhotoCommentMapper;
import com.example.myblog.mapper.UserMapper; // 导入 UserMapper
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.PhotoComment;
import com.example.myblog.model.User; // 导入 User model
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/photo/comment")
public class PhotoCommentController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoCommentController.class);

    @Resource
    private PhotoCommentMapper commentMapper;

    @Resource
    private UserMapper userMapper; // 注入 UserMapper

    // 根据图片ID查询评论
    @GetMapping("/photo/{photoId}")
    public BaseResponse<List<PhotoComment>> getCommentsByPhoto(@PathVariable Long photoId) {
        logger.info("Fetching comments for photo ID: {}", photoId);
        QueryWrapper<PhotoComment> wrapper = new QueryWrapper<>();
        wrapper.eq("photo_id", photoId);
        // 按照创建时间降序排列，以便最新评论显示在前面
        wrapper.orderByDesc("create_time");
        List<PhotoComment> comments = commentMapper.selectList(wrapper);

        // 填充评论者名称
        comments = comments.stream().map(comment -> {
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                comment.setAuthorName(author.getUsername());
                logger.debug("Photo Comment ID: {} - Author ID: {}, Author Name: {}", comment.getId(), comment.getAuthorId(), comment.getAuthorName());
            } else {
                comment.setAuthorName("未知用户");
                logger.warn("Photo Comment ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", comment.getId(), comment.getAuthorId());
            }
            return comment;
        }).collect(Collectors.toList());

        logger.info("Returning {} comments for photo ID: {}", comments.size(), photoId);
        return BaseResponse.success(comments);
    }

    // 根据id查询评论详情 (通常不直接使用，但保留)
    @GetMapping("/{id}")
    public BaseResponse<PhotoComment> getCommentById(@PathVariable Long id) {
        logger.info("Fetching photo comment by ID: {}", id);
        PhotoComment comment = commentMapper.selectById(id);
        if (comment != null) {
            // 填充评论者名称
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                comment.setAuthorName(author.getUsername());
            } else {
                comment.setAuthorName("未知用户");
            }
            return BaseResponse.success(comment);
        }
        logger.warn("Photo Comment ID: {} not found.", id);
        return BaseResponse.fail("评论不存在");
    }

    // 根据id删除评论
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteComment(@PathVariable Long id) {
        logger.info("Attempting to delete photo comment ID: {}", id);
        int deleted = commentMapper.deleteById(id);
        if (deleted > 0) {
            logger.info("Photo Comment ID: {} deleted successfully.", id);
            return BaseResponse.success("删除成功");
        }
        logger.warn("Failed to delete photo comment ID: {}", id);
        return BaseResponse.fail("删除失败");
    }

    // 发布评论
    @PostMapping("/publish")
    public BaseResponse<PhotoComment> publishComment(@RequestBody PhotoComment comment) {
        logger.info("Publishing new photo comment for photo ID: {} by author ID: {}", comment.getPhotoId(), comment.getAuthorId());
        int inserted = commentMapper.insert(comment);
        if (inserted > 0) {
            // 填充评论者名称
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                comment.setAuthorName(author.getUsername());
            } else {
                comment.setAuthorName("未知用户");
            }
            logger.info("Photo Comment published successfully, ID: {}", comment.getId());
            return BaseResponse.success(comment);
        }
        logger.error("Failed to publish photo comment for photo ID: {} by author ID: {}", comment.getPhotoId(), comment.getAuthorId());
        return BaseResponse.fail("评论发布失败");
    }
}
