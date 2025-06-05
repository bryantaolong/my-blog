package com.example.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myblog.model.Article; // 导入 Article 模型
import com.example.myblog.model.Photo;   // 导入 Photo 模型
import com.example.myblog.model.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 查询用户收藏的所有文章
     * @param userId 用户ID
     * @return 收藏的文章列表
     */
    @Select("SELECT a.* FROM article a JOIN user_favorites uf ON a.id = uf.item_id WHERE uf.user_id = #{userId} AND uf.item_type = 'ARTICLE'")
    List<Article> selectFavoriteArticles(@Param("userId") Long userId);

    /**
     * 查询用户收藏的所有图片
     * @param userId 用户ID
     * @return 收藏的图片列表
     */
    @Select("SELECT p.* FROM photo p JOIN user_favorites uf ON p.id = uf.item_id WHERE uf.user_id = #{userId} AND uf.item_type = 'PHOTO'")
    List<Photo> selectFavoritePhotos(@Param("userId") Long userId);
}
