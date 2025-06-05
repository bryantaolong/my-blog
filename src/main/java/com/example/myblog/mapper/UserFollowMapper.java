package com.example.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myblog.model.User; // 导入 User 模型
import com.example.myblog.model.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 查询指定用户关注的所有用户列表
     * @param followerId 关注者ID
     * @return 被关注的用户列表
     */
    @Select("SELECT u.* FROM user u JOIN user_follows uf ON u.id = uf.following_id WHERE uf.follower_id = #{followerId}")
    List<User> selectFollowingUsers(@Param("followerId") Long followerId);

    /**
     * 查询关注指定用户的所有粉丝列表
     * @param followingId 被关注者ID
     * @return 粉丝用户列表
     */
    @Select("SELECT u.* FROM user u JOIN user_follows uf ON u.id = uf.follower_id WHERE uf.following_id = #{followingId}")
    List<User> selectFollowerUsers(@Param("followingId") Long followingId);
}
