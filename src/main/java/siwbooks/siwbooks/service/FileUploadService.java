package siwbooks.siwbooks.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        logger.info("Starting file upload. File: {}, SubDir: {}", 
                   file.getOriginalFilename(), subDir);
        
        if (file.isEmpty()) {
            logger.warn("File is empty, returning null");
            return null;
        }
        
        // Validate file type
        String contentType = file.getContentType();
        logger.info("File content type: {}", contentType);
        
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.error("Invalid file type: {}", contentType);
            throw new IllegalArgumentException("Solo file immagine sono permessi");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subDir);
        logger.info("Upload path: {}", uploadPath.toAbsolutePath());
        
        if (!Files.exists(uploadPath)) {
            logger.info("Creating directory: {}", uploadPath);
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        logger.info("Generated filename: {}", filename);
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        logger.info("Saving file to: {}", filePath.toAbsolutePath());
        
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved successfully");
        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage(), e);
            throw e;
        }
        
        // Return relative path for storage in database
        String relativePath = subDir + "/" + filename;
        logger.info("Returning relative path: {}", relativePath);
        return relativePath;
    }
    
    public void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path path = Paths.get(uploadDir, filePath);
                logger.info("Deleting file: {}", path.toAbsolutePath());
                Files.deleteIfExists(path);
                logger.info("File deleted successfully");
            } catch (IOException e) {
                logger.error("Error deleting file: {}", filePath, e);
                System.err.println("Error deleting file: " + filePath);
            }
        }
    }
    
    public String getUploadDir() {
        return uploadDir;
    }
} 