package com.examconnect.service;

import com.examconnect.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(@Value("${storage.upload-dir:./uploads}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageLocation.resolve("materials"));
            Files.createDirectories(this.storageLocation.resolve("ai_docs"));
        } catch (IOException e) {
            log.error("Could not initialize upload storage directories", e);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence: " + originalFilename);
        }

        // Validate file extension for security
        String extension = "";
        int dotIdx = originalFilename.lastIndexOf('.');
        if (dotIdx > 0) {
            extension = originalFilename.substring(dotIdx).toLowerCase();
        }

        if (!extension.equals(".pdf") && !extension.equals(".txt") && !extension.equals(".doc") && !extension.equals(".docx")) {
            throw new BadRequestException("Only PDF, TXT, DOC, and DOCX files are allowed");
        }

        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        try {
            Path targetDir = this.storageLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);
            Path targetLocation = targetDir.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subDirectory + "/" + uniqueFilename;
        } catch (IOException e) {
            log.error("Failed to store file {}", originalFilename, e);
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }
    }

    public Path getFilePath(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return null;
        }
        String relative = fileUrl.substring("/uploads/".length());
        return this.storageLocation.resolve(relative).normalize();
    }
}
