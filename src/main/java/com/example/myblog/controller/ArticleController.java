package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.ArticleMapper;
import com.example.myblog.mapper.UserMapper; // 导入 UserMapper
import com.example.myblog.model.Article;
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.User; // 导入 User model
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper; // 注入 UserMapper

    // 查询所有文章
    @GetMapping("/list")
    public BaseResponse<List<Article>> getAllArticles() {
        logger.info("Received request for all articles.");
        List<Article> articles = articleMapper.selectList(null);

        // 填充作者名称
        articles = articles.stream().map(article -> {
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(author.getUsername());
                logger.info("Article ID: {} - Author ID: {}, Author Name: {}", article.getId(), article.getAuthorId(), article.getAuthorName());
            } else {
                article.setAuthorName("未知用户");
                logger.warn("Article ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", article.getId(), article.getAuthorId());
            }
            return article;
        }).collect(Collectors.toList());

        logger.info("Returning {} articles.", articles.size());
        return BaseResponse.success(articles);
    }

    // 根据用户ID查询文章
    @GetMapping("/user/{userId}")
    public BaseResponse<List<Article>> getArticlesByUser(@PathVariable String userId) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", userId);
        List<Article> articles = articleMapper.selectList(wrapper);

        // 填充作者名称
        articles = articles.stream().map(article -> {
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(author.getUsername());
                logger.info("Article ID: {} - Author ID: {}, Author Name: {}", article.getId(), article.getAuthorId(), article.getAuthorName());
            } else {
                article.setAuthorName("未知用户");
                logger.warn("Article ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", article.getId(), article.getAuthorId());
            }
            return article;
        }).collect(Collectors.toList());

        if (articles.isEmpty()) {
            return BaseResponse.fail("该用户没有发布文章");
        }
        return BaseResponse.success(articles);
    }

    // 根据ID查询文章
    @GetMapping("/id/{id}")
    public BaseResponse<Article> getArticleById(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            // 填充作者名称
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(author.getUsername());
                logger.info("Article ID: {} - Author ID: {}, Author Name: {}", article.getId(), article.getAuthorId(), article.getAuthorName());
            } else {
                article.setAuthorName("未知用户");
                logger.warn("Article ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", article.getId(), article.getAuthorId());
            }
            return BaseResponse.success(article);
        }
        return BaseResponse.fail("文章不存在");
    }

    // 新增：根据关键词搜索文章
    @GetMapping("/search")
    public BaseResponse<List<Article>> searchArticles(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return BaseResponse.fail("搜索关键词不能为空");
        }
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.like("title", query).or().like("content", query);
        List<Article> articles = articleMapper.selectList(wrapper);

        // 填充作者名称
        articles = articles.stream().map(article -> {
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(author.getUsername());
            } else {
                article.setAuthorName("未知用户");
            }
            return article;
        }).collect(Collectors.toList());

        return BaseResponse.success(articles);
    }

    // 发布文章
    @PostMapping("/publish")
    public BaseResponse<Article> publishArticle(@RequestBody Article article) {
        int inserted = articleMapper.insert(article);
        if (inserted > 0) {
            // 填充作者名称
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null) {
                article.setAuthorName(author.getUsername());
            } else {
                article.setAuthorName("未知用户");
            }
            return BaseResponse.success(article);
        }
        return BaseResponse.fail("发布文章失败");
    }

    // 更新文章
    @PutMapping("/update")
    public BaseResponse<Article> updateArticle(@RequestBody Article article) {
        if (article == null || article.getId() == null) {
            return BaseResponse.fail("更新失败：文章ID不能为空");
        }
        int updated = articleMapper.updateById(article);
        if (updated > 0) {
            // 返回更新后的文章，并填充作者名称
            Article updatedArticle = articleMapper.selectById(article.getId());
            User author = userMapper.selectById(updatedArticle.getAuthorId());
            if (author != null) {
                updatedArticle.setAuthorName(author.getUsername());
            } else {
                updatedArticle.setAuthorName("未知用户");
            }
            return BaseResponse.success(updatedArticle);
        }
        return BaseResponse.fail("更新失败");
    }

    // 删除文章
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteArticle(@PathVariable Long id) {
        int deleted = articleMapper.deleteById(id);
        if (deleted > 0) {
            return BaseResponse.success("文章删除成功");
        }
        return BaseResponse.fail("文章删除失败");
    }
}
