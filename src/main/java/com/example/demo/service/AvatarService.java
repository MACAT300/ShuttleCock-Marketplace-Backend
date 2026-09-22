package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class AvatarService {

    @Value("${app.image-cache-dir}")
    private String cacheDir;

    /**
     * 把上传的头像文件存到本地，返回可以直接访问的URL路径。
     */
    public String saveAvatar(int userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        String extension = getExtension(file.getOriginalFilename());
        String fileName = userId + extension;

        Path targetPath = Paths.get(cacheDir, "avatars", fileName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar: " + e.getMessage(), e);
        }

        return "/cached-images/avatars/" + fileName;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if (ext.matches("(?i)\\.(png|jpg|jpeg|webp)")) {
            return ext.toLowerCase();
        }
        return ".jpg";
    }
}