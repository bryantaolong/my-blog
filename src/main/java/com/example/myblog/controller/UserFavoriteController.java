package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.UserFavoriteMapper;
import com.example.myblog.model.Article;
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.Photo;
import com.example.myblog.model.UserFavorite;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class UserFavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteController.class);

    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    /**
     * 添加收藏
     * @param userFavorite 收藏对象，包含 user_id, item_type, item_id
     * @return 收藏结果
     */
    @PostMapping
    public BaseResponse<String> addFavorite(@RequestBody UserFavorite userFavorite) {
        if (userFavorite.getUserId() == null || userFavorite.getItemType() == null || userFavorite.getItemId() == null) {
            return BaseResponse.fail("用户ID、收藏类型和收藏项ID不能为空");
        }

        // 检查是否已收藏
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userFavorite.getUserId())
                .eq("item_type", userFavorite.getItemType())
                .eq("item_id", userFavorite.getItemId());
        if (userFavoriteMapper.selectCount(queryWrapper) > 0) {
            return BaseResponse.fail("已收藏该项");
        }

        int inserted = userFavoriteMapper.insert(userFavorite);
        if (inserted > 0) {
            logger.info("User {} added favorite: type={}, id={}", userFavorite.getUserId(), userFavorite.getItemType(), userFavorite.getItemId());
            return BaseResponse.success("收藏成功");
        }
        logger.error("Failed to add favorite for user {}: type={}, id={}", userFavorite.getUserId(), userFavorite.getItemType(), userFavorite.getItemId());
        return BaseResponse.fail("收藏失败");
    }

    /**
     * 取消收藏
     * 使用 @RequestParam 接收参数，因为 DELETE 请求通常不带 @RequestBody
     * @param userId 收藏者ID
     * @param itemType 收藏项类型
     * @param itemId 收藏项ID
     * @return 取消收藏结果
     */
    @DeleteMapping
    public BaseResponse<String> removeFavorite(
            @RequestParam("userId") Long userId,
            @RequestParam("itemType") String itemType,
            @RequestParam("itemId") Long itemId) {

        if (userId == null || itemType == null || itemId == null) {
            return BaseResponse.fail("用户ID、收藏类型和收藏项ID不能为空");
        }

        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("item_type", itemType)
                .eq("item_id", itemId);

        int deleted = userFavoriteMapper.delete(queryWrapper);
        if (deleted > 0) {
            logger.info("User {} removed favorite: type={}, id={}", userId, itemType, itemId);
            return BaseResponse.success("取消收藏成功");
        }
        logger.warn("Failed to remove favorite for user {}: type={}, id={}. Not found or already removed.", userId, itemType, itemId);
        return BaseResponse.fail("取消收藏失败或未收藏该项");
    }

    /**
     * 检查用户是否收藏了某个项
     * @param userId 用户ID
     * @param itemType 收藏项类型
     * @param itemId 收藏项ID
     * @return true 如果已收藏，false 否则
     */
    @GetMapping("/isFavorited/{userId}/{itemType}/{itemId}")
    public BaseResponse<Boolean> isFavorited(
            @PathVariable Long userId,
            @PathVariable String itemType,
            @PathVariable Long itemId) {
        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("item_type", itemType)
                .eq("item_id", itemId);
        boolean isFavorited = userFavoriteMapper.selectCount(queryWrapper) > 0;
        return BaseResponse.success(isFavorited);
    }

    /**
     * 获取用户收藏的所有文章
     * @param userId 用户ID
     * @return 收藏的文章列表
     */
    @GetMapping("/articles/{userId}")
    public BaseResponse<List<Article>> getFavoriteArticles(@PathVariable Long userId) {
        List<Article> articles = userFavoriteMapper.selectFavoriteArticles(userId);
        // 为了安全，不返回密码等敏感信息
        articles.forEach(article -> {
            // 这里可以根据需要清除敏感信息，例如作者密码等
//             article.getAuthor().setPassword(null); // 如果 Article 模型中包含 User 对象
        });
        return BaseResponse.success(articles);
    }

    /**
     * 获取用户收藏的所有图片
     * @param userId 用户ID
     * @return 收藏的图片列表
     */
    @GetMapping("/photos/{userId}")
    public BaseResponse<List<Photo>> getFavoritePhotos(@PathVariable Long userId) {
        List<Photo> photos = userFavoriteMapper.selectFavoritePhotos(userId);
        // 为了安全，不返回密码等敏感信息
        photos.forEach(photo -> {
//             photo.getAuthor().setPassword(null); // 如果 Photo 模型中包含 User 对象
        });
        return BaseResponse.success(photos);
    }
}
