package com.example.demo.service;

import com.example.demo.model.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageCacheService {

    @Value("${app.image-cache-dir}")
    private String cacheDir;

    public String getCachedImageUrl(Image image) {

        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        if (image.getUrl() == null || image.getUrl().isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be empty");
        }

        String imageId = String.valueOf(image.getId());

        return cacheAndGetUrl(
                "images",
                imageId,
                image.getUrl()
        );
    }

    private String cacheAndGetUrl(
            String subFolder,
            String baseName,
            String remoteUrl) {

        String extension = getExtension(remoteUrl);

        String fileName = baseName + extension;

        Path filePath = Paths.get(
                cacheDir,
                subFolder,
                fileName
        );

        // Already cached
        if (Files.exists(filePath)) {
            return "/cached-images/"
                    + subFolder
                    + "/"
                    + fileName;
        }

        try {
            Files.createDirectories(filePath.getParent());

            downloadFile(
                    remoteUrl,
                    filePath
            );

        } catch (IOException | InterruptedException e) {

            throw new RuntimeException(
                    "Failed to cache image: "
                            + e.getMessage(),
                    e
            );
        }

        return "/cached-images/"
                + subFolder
                + "/"
                + fileName;
    }

    private void downloadFile(
            String url,
            Path targetPath)
            throws IOException, InterruptedException {

        HttpClient client =
                HttpClient.newBuilder()
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
                        .build();

        HttpRequest request =
                HttpRequest.newBuilder(
                                URI.create(url)
                        )
                        // Add User-Agent to fake the Website
                        .header("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                        "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Referer", url)
                        .GET()
                        .build();

        HttpResponse<InputStream> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofInputStream()
                );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Failed to download image, status: "
                            + response.statusCode()
            );
        }

        try (InputStream in = response.body()) {

            Files.copy(
                    in,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private String getExtension(String url) {

        if (url == null) {
            return ".png";
        }

        // Remove query string
        String cleanUrl = url;

        if (cleanUrl.contains("?")) {
            cleanUrl = cleanUrl.substring(
                    0,
                    cleanUrl.indexOf('?')
            );
        }

        // Only inspect the last part of the URL
        String lastSegment =
                cleanUrl.substring(
                        cleanUrl.lastIndexOf('/') + 1
                );

        if (lastSegment.contains(".")) {

            String ext =
                    lastSegment.substring(
                            lastSegment.lastIndexOf('.')
                    );

            if (ext.matches(
                    "(?i)\\.(png|jpg|jpeg|gif|webp|bmp)"
            )) {
                return ext.toLowerCase();
            }
        }

        // Default
        return ".jpg";
    }
}