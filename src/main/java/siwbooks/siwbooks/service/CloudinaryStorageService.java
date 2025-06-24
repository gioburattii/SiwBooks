package siwbooks.siwbooks.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Profile("prod")  // Attivo solo in produzione
public class CloudinaryStorageService implements IFileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryStorageService.class);
    
    @Autowired
    private Cloudinary cloudinary;
    
    @Override
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        logger.info("Starting file upload to Cloudinary. File: {}, SubDir: {}", 
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
        
        try {
            // Upload to Cloudinary with folder structure
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "folder", "siwbooks/" + subDir,
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false,
                    "resource_type", "auto"
                )
            );
            
            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");
            
            logger.info("File uploaded successfully. PublicId: {}, URL: {}", publicId, url);
            
            // Return the URL directly since Cloudinary handles the file serving
            return url;
            
        } catch (IOException e) {
            logger.error("Error uploading file to Cloudinary: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            try {
                // Extract public_id from URL
                // URL format: https://res.cloudinary.com/{cloud_name}/{resource_type}/upload/{version}/{public_id}.{format}
                String publicId = extractPublicIdFromUrl(fileUrl);
                
                if (publicId != null) {
                    logger.info("Deleting file from Cloudinary: {}", publicId);
                    Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    logger.info("File deleted successfully. Result: {}", deleteResult);
                }
            } catch (Exception e) {
                logger.error("Error deleting file from Cloudinary: {}", fileUrl, e);
            }
        }
    }
    
    private String extractPublicIdFromUrl(String url) {
        try {
            // Extract public_id from Cloudinary URL
            if (url.contains("cloudinary.com/")) {
                String[] parts = url.split("/upload/");
                if (parts.length > 1) {
                    String pathAfterUpload = parts[1];
                    // Remove version if present (e.g., v1234567890/)
                    if (pathAfterUpload.matches("v\\d+/.*")) {
                        pathAfterUpload = pathAfterUpload.substring(pathAfterUpload.indexOf('/') + 1);
                    }
                    // Remove file extension
                    int lastDotIndex = pathAfterUpload.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        return pathAfterUpload.substring(0, lastDotIndex);
                    }
                    return pathAfterUpload;
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting public_id from URL: {}", url, e);
        }
        return null;
    }
} 