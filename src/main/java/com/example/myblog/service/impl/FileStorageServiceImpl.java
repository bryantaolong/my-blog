package com.example.myblog.service.impl;

import com.example.myblog.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // 构建上传目录的绝对路径，包括子目录
        // 例如：/path/to/your/project/uploads/user_1
        Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();

        // 如果目录不存在，则创建
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String correctedFilename = originalFilename;

        // 检查并修正文件扩展名中的 .jepg 错误为 .jpeg
        if (originalFilename != null && originalFilename.toLowerCase().endsWith(".jepg")) {
            correctedFilename = originalFilename.substring(0, originalFilename.length() - 5) + ".jpeg";
        }

        // 生成唯一的文件名，以时间戳开头，加上修正后的原始文件名
        String fileName = System.currentTimeMillis() + "_" + correctedFilename;
        // 构建文件的完整物理路径
        Path filePath = uploadPath.resolve(fileName);

        // 将文件输入流复制到目标路径，如果文件已存在则替换
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 返回文件在 uploads 目录下的相对路径
        // 例如：user_1/1748951631901_Dying Light.jpeg
        return Paths.get(subDirectory, fileName).toString();
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            // 构建文件的完整物理路径
            Path fullPath = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
            // 删除文件，如果文件不存在则返回 false
            return Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            // 打印异常信息，但返回 false 表示删除失败
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] loadFileAsBytes(String filePath) throws IOException {
        // 构建文件的完整物理路径
        Path fullPath = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
        // 读取文件所有字节
        return Files.readAllBytes(fullPath);
    }
}
