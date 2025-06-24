package siwbooks.siwbooks.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IFileStorageService {
    String saveFile(MultipartFile file, String subDir) throws IOException;
    void deleteFile(String filePath);
} 