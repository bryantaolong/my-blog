package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.UserFollowMapper;
import com.example.myblog.mapper.UserMapper; // 导入 UserMapper，用于获取用户详情
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.User; // 导入 User 模型
import com.example.myblog.model.UserFollow;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class UserFollowController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowController.class);

    @Resource
    private UserFollowMapper userFollowMapper;
    @Resource
    private UserMapper userMapper; // 用于获取用户详情，例如在关注/粉丝列表中

    /**
     * 关注一个用户
     * @param userFollow 包含关注者ID (followerId) 和被关注者ID (followingId)
     * @return 关注结果
     */
    @PostMapping
    public BaseResponse<String> followUser(@RequestBody UserFollow userFollow) {
        if (userFollow.getFollowerId() == null || userFollow.getFollowingId() == null) {
            return BaseResponse.fail("关注者ID和被关注者ID不能为空");
        }
        if (userFollow.getFollowerId().equals(userFollow.getFollowingId())) {
            return BaseResponse.fail("不能关注自己");
        }

        // 检查是否已关注
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userFollow.getFollowerId())
                .eq("following_id", userFollow.getFollowingId());
        if (userFollowMapper.selectCount(queryWrapper) > 0) {
            return BaseResponse.fail("已关注该用户");
        }

        int inserted = userFollowMapper.insert(userFollow);
        if (inserted > 0) {
            logger.info("User {} followed user {}", userFollow.getFollowerId(), userFollow.getFollowingId());
            return BaseResponse.success("关注成功");
        }
        logger.error("Failed to follow user {} by {}", userFollow.getFollowingId(), userFollow.getFollowerId());
        return BaseResponse.fail("关注失败");
    }

    /**
     * 取消关注一个用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 取消关注结果
     */
    @DeleteMapping("/{followerId}/{followingId}") // 修改此处，使用路径变量
    public BaseResponse<String> unfollowUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        if (followerId == null || followingId == null) { // 参数校验
            return BaseResponse.fail("关注者ID和被关注者ID不能为空");
        }

        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId) // 使用传入的 followerId
                .eq("following_id", followingId); // 使用传入的 followingId

        int deleted = userFollowMapper.delete(queryWrapper);
        if (deleted > 0) {
            logger.info("User {} unfollowed user {}", followerId, followingId);
            return BaseResponse.success("取消关注成功");
        }
        logger.warn("Failed to unfollow user {} by {}: not found or already unfollowed", followingId, followerId);
        return BaseResponse.fail("取消关注失败或未关注该用户");
    }

    /**
     * 获取指定用户关注的所有用户列表
     * @param userId 关注者ID
     * @return 被关注的用户列表
     */
    @GetMapping("/following/{userId}")
    public BaseResponse<List<User>> getFollowingUsers(@PathVariable Long userId) {
        List<User> followingUsers = userFollowMapper.selectFollowingUsers(userId);
        // 为了安全，不返回密码
        followingUsers.forEach(user -> user.setPassword(null));
        return BaseResponse.success(followingUsers);
    }

    /**
     * 获取关注指定用户的所有粉丝列表
     * @param userId 被关注者ID
     * @return 粉丝用户列表
     */
    @GetMapping("/followers/{userId}")
    public BaseResponse<List<User>> getFollowerUsers(@PathVariable Long userId) {
        List<User> followerUsers = userFollowMapper.selectFollowerUsers(userId);
        // 为了安全，不返回密码
        followerUsers.forEach(user -> user.setPassword(null));
        return BaseResponse.success(followerUsers);
    }

    /**
     * 检查一个用户是否关注了另一个用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return true 如果已关注，false 否则
     */
    @GetMapping("/isFollowing/{followerId}/{followingId}")
    public BaseResponse<Boolean> isFollowing(@PathVariable Long followerId, @PathVariable Long followingId) {
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                .eq("following_id", followingId);
        boolean isFollowing = userFollowMapper.selectCount(queryWrapper) > 0;
        return BaseResponse.success(isFollowing);
    }
}
