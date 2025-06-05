package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.UserMapper;
import com.example.myblog.model.*;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserMapper userMapper;

    // 登录
    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername())
                .eq("password", user.getPassword());
        User loginUser = userMapper.selectOne(wrapper);
        if (loginUser != null) {
            logger.info("User logged in: {}", loginUser.getUsername());
            return BaseResponse.success(loginUser);
        }
        logger.warn("Login failed for username: {}", user.getUsername());
        return BaseResponse.fail("用户名或密码错误");
    }

    // 注册
    @PostMapping("/register")
    public BaseResponse<User> register(@RequestBody User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            logger.warn("Registration failed: Username {} already exists.", user.getUsername());
            return BaseResponse.fail("用户名已存在");
        }

        // 设置默认权限和默认简介
        user.setRole("USER"); // 默认注册为普通用户
        user.setBio("这里是个人简介，一句话介绍自己。"); // 注册时设置默认简介

        int inserted = userMapper.insert(user);
        if (inserted > 0) {
            logger.info("User registered: {}", user.getUsername());
            return BaseResponse.success(user);
        }
        logger.error("Registration failed for user: {}", user.getUsername());
        return BaseResponse.fail("注册失败");
    }

    // 修改密码
    @PutMapping("/change-password")
    public BaseResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request == null || request.getUserId() == null || request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return BaseResponse.fail("用户ID和新密码不能为空");
        }

        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            return BaseResponse.fail("用户不存在");
        }

        // 可以在这里添加旧密码验证逻辑，如果前端提供了旧密码
        if (request.getOldPassword() != null && !user.getPassword().equals(request.getOldPassword())) {
            return BaseResponse.fail("旧密码不正确");
        }

        user.setPassword(request.getNewPassword());
        int updated = userMapper.updateById(user);

        if (updated > 0) {
            logger.info("Password changed successfully for user ID: {}", request.getUserId());
            return BaseResponse.success("密码修改成功");
        }
        logger.error("Failed to change password for user ID: {}", request.getUserId());
        return BaseResponse.fail("密码修改失败");
    }

    // 获取用户详情
    @GetMapping("/{id}")
    public BaseResponse<User> getUser(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return new BaseResponse<>(404, "用户不存在", null);
        }
        // 为了安全，不返回密码
        user.setPassword(null);
        return new BaseResponse<>(200, "成功", user);
    }

    // 新增：更新用户个人信息 (例如简介 bio)
    @PutMapping("/update-profile")
    public BaseResponse<User> updateUserProfile(@RequestBody User user) {
        if (user == null || user.getId() == null) {
            return BaseResponse.fail("用户ID不能为空");
        }

        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            return BaseResponse.fail("用户不存在");
        }

        // 只更新允许修改的字段，例如 bio
        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }
        // 如果还需要更新其他字段，可以在这里添加类似逻辑

        int updated = userMapper.updateById(existingUser);

        if (updated > 0) {
            logger.info("User profile updated successfully for user ID: {}", user.getId());
            // 返回更新后的用户对象，不包含密码
            existingUser.setPassword(null);
            return BaseResponse.success(existingUser);
        }
        logger.error("Failed to update user profile for user ID: {}", user.getId());
        return BaseResponse.fail("个人信息更新失败");
    }

    // 新增：根据关键词搜索用户
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return BaseResponse.fail("搜索关键词不能为空");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", query); // 根据用户名模糊搜索
        List<User> users = userMapper.selectList(wrapper);

        // 移除密码等敏感信息
        users.forEach(user -> user.setPassword(null));

        return BaseResponse.success(users);
    }


    // 用于修改密码请求的内部类，接收用户ID和新密码
    @Setter
    public static class ChangePasswordRequest {
        @Getter
        private Long userId;
        @Getter
        private String newPassword;
        @Getter
        private String oldPassword;
    }
}
