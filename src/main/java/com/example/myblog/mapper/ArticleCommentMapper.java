package com.example.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myblog.model.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}
