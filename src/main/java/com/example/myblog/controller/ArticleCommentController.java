package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.ArticleCommentMapper;
import com.example.myblog.mapper.UserMapper; // 导入 UserMapper
import com.example.myblog.model.ArticleComment;
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.User; // 导入 User model
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article/comment")
public class ArticleCommentController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCommentController.class);

    @Resource
    private ArticleCommentMapper commentMapper;

    @Resource
    private UserMapper userMapper; // 注入 UserMapper

    // 根据文章ID查询评论
    @GetMapping("/article/{articleId}")
    public BaseResponse<List<ArticleComment>> getCommentsByArticle(@PathVariable Long articleId) {
        logger.info("Fetching comments for article ID: {}", articleId);
        QueryWrapper<ArticleComment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        // 按照创建时间降序排列，以便最新评论显示在前面
        wrapper.orderByDesc("create_time");
        List<ArticleComment> comments = commentMapper.selectList(wrapper);

        // 填充评论者名称
        comments = comments.stream().map(comment -> {
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                comment.setAuthorName(author.getUsername());
                logger.debug("Comment ID: {} - Author ID: {}, Author Name: {}", comment.getId(), comment.getAuthorId(), comment.getAuthorName());
            } else {
                comment.setAuthorName("未知用户");
                logger.warn("Comment ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", comment.getId(), comment.getAuthorId());
            }
            return comment;
        }).collect(Collectors.toList());

        logger.info("Returning {} comments for article ID: {}", comments.size(), articleId);
        return BaseResponse.success(comments);
    }

    // 根据id查询评论详情 (通常不直接使用，但保留)
    @GetMapping("/{id}")
    public BaseResponse<ArticleComment> getCommentById(@PathVariable Long id) {
        logger.info("Fetching comment by ID: {}", id);
        ArticleComment comment = commentMapper.selectById(id);
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
        logger.warn("Comment ID: {} not found.", id);
        return BaseResponse.fail("评论不存在");
    }

    // 根据id删除评论
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteComment(@PathVariable Long id) {
        logger.info("Attempting to delete comment ID: {}", id);
        int deleted = commentMapper.deleteById(id);
        if (deleted > 0) {
            logger.info("Comment ID: {} deleted successfully.", id);
            return BaseResponse.success("删除成功");
        }
        logger.warn("Failed to delete comment ID: {}", id);
        return BaseResponse.fail("删除失败");
    }

    // 发布评论
    @PostMapping("/publish")
    public BaseResponse<ArticleComment> publishComment(@RequestBody ArticleComment comment) {
        logger.info("Publishing new comment for article ID: {} by author ID: {}", comment.getArticleId(), comment.getAuthorId());
        int inserted = commentMapper.insert(comment);
        if (inserted > 0) {
            // 填充评论者名称
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                comment.setAuthorName(author.getUsername());
            } else {
                comment.setAuthorName("未知用户");
            }
            logger.info("Comment published successfully, ID: {}", comment.getId());
            return BaseResponse.success(comment);
        }
        logger.error("Failed to publish comment for article ID: {} by author ID: {}", comment.getArticleId(), comment.getAuthorId());
        return BaseResponse.fail("评论发布失败");
    }
}
