package com.example.myblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.mapper.PhotoMapper;
import com.example.myblog.model.BaseResponse;
import com.example.myblog.model.Photo;
import com.example.myblog.service.FileStorageService;
import com.example.myblog.mapper.UserMapper; // 导入 UserMapper
import com.example.myblog.model.User; // 导入 User model
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger; // 导入 Logger
import org.slf4j.LoggerFactory; // 导入 LoggerFactory

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class); // 初始化 Logger

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private FileStorageService fileStorageService;

    @Resource // 注入 UserMapper 以便查询用户名
    private UserMapper userMapper;

    // 根据用户id查询图片
    @GetMapping("/user/{userId}")
    public BaseResponse<List<Photo>> getPhotosByUser(@PathVariable String userId) {
        QueryWrapper<Photo> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", userId);
        List<Photo> photos = photoMapper.selectList(wrapper);

        // 填充作者名称
        photos = photos.stream().map(photo -> {
            User author = userMapper.selectById(photo.getAuthorId());
            if (author != null) {
                photo.setAuthorName(author.getUsername());
                logger.info("Photo ID: {} - Author ID: {}, Author Name: {}", photo.getId(), photo.getAuthorId(), photo.getAuthorName());
            } else {
                photo.setAuthorName("未知用户");
                logger.warn("Photo ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", photo.getId(), photo.getAuthorId());
            }
            return photo;
        }).collect(Collectors.toList());

        if (photos.isEmpty()) {
            return BaseResponse.fail("该用户没有上传图片");
        }
        return BaseResponse.success(photos);
    }

    // 查询所有图片 (新增，用于首页显示所有图片)
    @GetMapping("/list") // 确保这里是 "/list"，与 PhotoService 中的定义匹配
    public BaseResponse<List<Photo>> getAllPhotos() {
        logger.info("Received request for all photos.");
        List<Photo> photos = photoMapper.selectList(null);

        // 填充作者名称
        photos = photos.stream().map(photo -> {
            User author = userMapper.selectById(photo.getAuthorId());
            if (author != null) {
                photo.setAuthorName(author.getUsername());
                logger.info("Photo ID: {} - Author ID: {}, Author Name: {}", photo.getId(), photo.getAuthorId(), photo.getAuthorName());
            } else {
                photo.setAuthorName("未知用户");
                logger.warn("Photo ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", photo.getId(), photo.getAuthorId());
            }
            return photo;
        }).collect(Collectors.toList());

        logger.info("Returning {} photos.", photos.size());
        return BaseResponse.success(photos);
    }

    // 根据id查询图片
    @GetMapping("/{id}")
    public BaseResponse<Photo> getPhotoById(@PathVariable Long id) {
        Photo photo = photoMapper.selectById(id);
        if (photo != null) {
            // 填充作者名称
            User author = userMapper.selectById(photo.getAuthorId());
            if (author != null) {
                photo.setAuthorName(author.getUsername());
                logger.info("Photo ID: {} - Author ID: {}, Author Name: {}", photo.getId(), photo.getAuthorId(), photo.getAuthorName());
            } else {
                photo.setAuthorName("未知用户");
                logger.warn("Photo ID: {} - Author ID: {} not found in User table. Setting author name to '未知用户'.", photo.getId(), photo.getAuthorId());
            }
            return BaseResponse.success(photo);
        }
        return BaseResponse.fail("图片不存在");
    }

    // 新增：根据关键词搜索图片
    @GetMapping("/search")
    public BaseResponse<List<Photo>> searchPhotos(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return BaseResponse.fail("搜索关键词不能为空");
        }
        QueryWrapper<Photo> wrapper = new QueryWrapper<>();
        wrapper.like("name", query).or().like("description", query);
        List<Photo> photos = photoMapper.selectList(wrapper);

        // 填充作者名称
        photos = photos.stream().map(photo -> {
            User author = userMapper.selectById(photo.getAuthorId());
            if (author != null) {
                photo.setAuthorName(author.getUsername());
            } else {
                photo.setAuthorName("未知用户");
            }
            return photo;
        }).collect(Collectors.toList());

        return BaseResponse.success(photos);
    }

    // 根据id删除图片
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deletePhoto(@PathVariable Long id) {
        Photo photoToDelete = photoMapper.selectById(id);
        if (photoToDelete == null) {
            return BaseResponse.fail("要删除的图片不存在");
        }

        try {
            boolean fileDeleted = fileStorageService.deleteFile(photoToDelete.getFilePath());
            if (!fileDeleted) {
                logger.warn("Warning: Could not delete physical file for photo ID: {}, path: {}", id, photoToDelete.getFilePath());
            }

            int deleted = photoMapper.deleteById(id);
            if (deleted > 0) {
                return BaseResponse.success("图片及关联文件删除成功");
            } else {
                return BaseResponse.fail("数据库记录删除失败");
            }
        } catch (Exception e) {
            logger.error("Error deleting photo ID: {}. Error: {}", id, e.getMessage(), e);
            return BaseResponse.fail("删除图片过程中发生错误: " + e.getMessage());
        }
    }

    // 根据id修改图片信息 (只修改数据库记录，不涉及文件本身)
    @PutMapping("/update")
    public BaseResponse<Photo> updatePhoto(@RequestBody Photo photo) {
        if (photo == null || photo.getId() == null) {
            return BaseResponse.fail("更新失败：图片ID不能为空");
        }
        Photo existingPhoto = photoMapper.selectById(photo.getId());
        if (existingPhoto == null) {
            return BaseResponse.fail("更新失败：图片不存在");
        }

        existingPhoto.setName(photo.getName());
        existingPhoto.setDescription(photo.getDescription());

        int updated = photoMapper.updateById(photo);
        if (updated > 0) {
            Photo updatedPhoto = photoMapper.selectById(photo.getId());
            User author = userMapper.selectById(updatedPhoto.getAuthorId());
            if (author != null) {
                updatedPhoto.setAuthorName(author.getUsername());
            } else {
                updatedPhoto.setAuthorName("未知用户");
            }
            return BaseResponse.success(updatedPhoto);
        }
        return BaseResponse.fail("更新失败");
    }

    // 上传图片
    @PostMapping("/upload")
    public BaseResponse<Photo> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("authorId") String authorId) {
        try {
            // 验证参数
            if (file.isEmpty() || name == null || name.trim().isEmpty() || authorId == null || authorId.trim().isEmpty()) {
                return BaseResponse.fail(HttpStatus.BAD_REQUEST.value(), "文件、名称和作者ID不能为空");
            }

            // 验证文件类型是否为PNG
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equalsIgnoreCase("image/png")) {
                return BaseResponse.fail(HttpStatus.BAD_REQUEST.value(), "只支持PNG格式的图片");
            }

            // 使用正斜杠作为路径分隔符
            String subDirectory = authorId.replace("\\", "/");

            // 存储文件
            String filePath = fileStorageService.storeFile(file, subDirectory);

            // 确保路径使用正斜杠
            filePath = filePath.replace("\\", "/");

            // 创建图片记录
            Photo photo = new Photo();
            photo.setName(name);
            photo.setDescription(description);
            photo.setFilePath(filePath);
            photo.setFileType("image/png"); // 强制设置为image/png
            photo.setFileSize(file.getSize());
            photo.setAuthorId(authorId);

            // 设置作者名称
            User author = userMapper.selectById(authorId);
            if (author != null) {
                photo.setAuthorName(author.getUsername());
            } else {
                photo.setAuthorName("未知用户");
            }

            // 保存到数据库
            int inserted = photoMapper.insert(photo);

            if (inserted > 0) {
                return BaseResponse.success(photo);
            } else {
                // 回滚：删除已上传的文件
                fileStorageService.deleteFile(filePath);
                return BaseResponse.fail("图片信息保存到数据库失败");
            }

        } catch (IOException e) {
            logger.error("File upload failed: {}", e.getMessage(), e);
            return BaseResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing image upload request: {}", e.getMessage(), e);
            return BaseResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "处理图片上传请求失败: " + e.getMessage());
        }
    }
}
