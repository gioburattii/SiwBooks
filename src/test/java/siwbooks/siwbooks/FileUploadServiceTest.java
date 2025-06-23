package siwbooks.siwbooks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import siwbooks.siwbooks.service.FileUploadService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadServiceTest {

    private FileUploadService fileUploadService;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService();
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
    }

    @Test
    void testSaveImageFile() throws IOException {
        // Create a mock image file
        byte[] imageContent = "fake image content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test.jpg", 
            "image/jpeg", 
            imageContent
        );

        // Save the file
        String savedPath = fileUploadService.saveFile(mockFile, "books");

        // Verify the file was saved
        assertNotNull(savedPath);
        assertTrue(savedPath.startsWith("books/"));
        assertTrue(savedPath.endsWith(".jpg"));

        // Verify the file exists on disk
        Path fullPath = tempDir.resolve(savedPath);
        assertTrue(Files.exists(fullPath));
        
        // Verify the content
        byte[] savedContent = Files.readAllBytes(fullPath);
        assertArrayEquals(imageContent, savedContent);
    }

    @Test
    void testSaveEmptyFile() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image", 
            "empty.jpg", 
            "image/jpeg", 
            new byte[0]
        );

        String result = fileUploadService.saveFile(emptyFile, "books");
        assertNull(result);
    }

    @Test
    void testSaveNonImageFile() {
        MockMultipartFile textFile = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "text content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(textFile, "books");
        });
    }

    @Test
    void testDeleteFile() throws IOException {
        // First create a file
        byte[] content = "test content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test.png", 
            "image/png", 
            content
        );

        String savedPath = fileUploadService.saveFile(mockFile, "books");
        Path fullPath = tempDir.resolve(savedPath);
        assertTrue(Files.exists(fullPath));

        // Now delete it
        fileUploadService.deleteFile(savedPath);
        assertFalse(Files.exists(fullPath));
    }
} 