package com.example.myblog.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory) throws IOException;
    boolean deleteFile(String filePath);
    byte[] loadFileAsBytes(String filePath) throws IOException;
}