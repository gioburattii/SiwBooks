package siwbooks.siwbooks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.model.Author;
import siwbooks.siwbooks.repository.BookRepository;
import siwbooks.siwbooks.repository.AuthorRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> findByAuthorName(String authorName) {
        return bookRepository.findByAuthorName(authorName);
    }
    
    public List<Book> findByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }
    
    public List<Book> findByPublicationYearRange(Integer startYear, Integer endYear) {
        return bookRepository.findByPublicationYearBetween(startYear, endYear);
    }
    
    public List<Book> findBooksOrderedByRating() {
        return bookRepository.findBooksOrderedByAverageRating();
    }
    
    public Book save(Book book) {
        return bookRepository.save(book);
    }
    
    public Book createBook(String title, Integer publicationYear, Set<Long> authorIds, 
                          List<MultipartFile> imageFiles) throws IOException {
        Book book = new Book(title, publicationYear);
        
        if (authorIds != null && !authorIds.isEmpty()) {
            Set<Author> authors = Set.copyOf(authorRepository.findAllById(authorIds));
            book.setAuthors(authors);
        }
        
        // Handle image uploads
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String imagePath = fileUploadService.saveFile(imageFile, "books");
                    if (imagePath != null) {
                        imagePaths.add(imagePath);
                    }
                }
            }
            book.setImagePaths(imagePaths);
        }
        
        return save(book);
    }
    
    public Book updateBook(Long id, String title, Integer publicationYear, Set<Long> authorIds,
                          List<MultipartFile> imageFiles) throws IOException {
        logger.info("Starting updateBook for id: {}, title: {}, year: {}, authorIds: {}", 
                   id, title, publicationYear, authorIds);
        
        try {
            Optional<Book> optionalBook = findById(id);
            if (optionalBook.isEmpty()) {
                logger.error("Book not found with id: {}", id);
                throw new RuntimeException("Book not found with id: " + id);
            }
            
            Book book = optionalBook.get();
            logger.info("Found book: {} (id: {})", book.getTitle(), book.getId());
            
            // Validate input parameters
            if (title == null || title.trim().isEmpty()) {
                logger.error("Title is null or empty");
                throw new IllegalArgumentException("Il titolo del libro non può essere vuoto");
            }
            
            if (publicationYear == null) {
                logger.error("Publication year is null");
                throw new IllegalArgumentException("L'anno di pubblicazione non può essere vuoto");
            }
            
            if (publicationYear < 1000) {
                logger.error("Publication year is too small: {}", publicationYear);
                throw new IllegalArgumentException("L'anno di pubblicazione deve essere almeno 1000");
            }
            
            // Update basic properties
            try {
                book.setTitle(title.trim());
                book.setPublicationYear(publicationYear);
                logger.info("Updated basic book info: title={}, year={}", book.getTitle(), book.getPublicationYear());
            } catch (Exception e) {
                logger.error("Error updating basic book properties: {}", e.getMessage(), e);
                throw new RuntimeException("Errore nell'aggiornamento delle proprietà base: " + e.getMessage());
            }
            
            // Handle authors
            try {
                if (authorIds != null && !authorIds.isEmpty()) {
                    logger.info("Processing author IDs: {}", authorIds);
                    
                    // First, clear existing authors by setting to empty set
                    book.setAuthors(new HashSet<>());
                    
                    // Save the book first to clear the relationships
                    book = bookRepository.save(book);
                    logger.info("Cleared existing authors and saved book");
                    
                    // Now fetch the authors and set them
                    List<Author> authorList = authorRepository.findAllById(authorIds);
                    logger.info("Found {} authors in database", authorList.size());
                    
                    if (authorList.size() != authorIds.size()) {
                        logger.error("Some author IDs not found. Requested: {}, Found: {}", 
                                   authorIds.size(), authorList.size());
                        throw new RuntimeException("Alcuni autori specificati non esistono nel database");
                    }
                    
                    // Set the new authors
                    Set<Author> authors = new HashSet<>(authorList);
                    book.setAuthors(authors);
                    logger.info("Set {} authors for the book", authors.size());
                } else {
                    logger.info("No author IDs provided, clearing authors");
                    book.setAuthors(new HashSet<>());
                }
            } catch (Exception e) {
                logger.error("Error processing authors: {}", e.getMessage(), e);
                throw new RuntimeException("Errore nel processare gli autori: " + e.getMessage());
            }
            
            // Handle image uploads - only if new images are actually provided
            try {
                if (imageFiles != null && !imageFiles.isEmpty()) {
                    logger.info("Processing {} image files", imageFiles.size());
                    // Check if there are actually new images (not just empty files)
                    boolean hasNewImages = imageFiles.stream().anyMatch(file -> !file.isEmpty());
                    logger.info("Has new images: {}", hasNewImages);
                    
                    if (hasNewImages) {
                        // Delete old images only if new ones are uploaded
                        if (book.getImagePaths() != null) {
                            logger.info("Deleting {} old images", book.getImagePaths().size());
                            for (String imagePath : book.getImagePaths()) {
                                fileUploadService.deleteFile(imagePath);
                            }
                        }
                        
                        List<String> imagePaths = new ArrayList<>();
                        for (MultipartFile imageFile : imageFiles) {
                            if (!imageFile.isEmpty()) {
                                logger.info("Processing image file: {}", imageFile.getOriginalFilename());
                                String imagePath = fileUploadService.saveFile(imageFile, "books");
                                if (imagePath != null) {
                                    imagePaths.add(imagePath);
                                    logger.info("Saved image with path: {}", imagePath);
                                } else {
                                    logger.warn("Failed to save image file: {}", imageFile.getOriginalFilename());
                                }
                            }
                        }
                        book.setImagePaths(imagePaths);
                        logger.info("Set {} new image paths", imagePaths.size());
                    }
                    // If no new images are provided, keep the existing ones
                } else {
                    logger.info("No image files provided, keeping existing images");
                }
            } catch (Exception e) {
                logger.error("Error processing image files: {}", e.getMessage(), e);
                throw new RuntimeException("Errore nel processare le immagini: " + e.getMessage());
            }
            
            // Save the book
            try {
                logger.info("Attempting to save book with id: {}", book.getId());
                logger.info("Book state before save: title={}, year={}, authors={}, imagePaths={}", 
                           book.getTitle(), book.getPublicationYear(), 
                           book.getAuthors() != null ? book.getAuthors().size() : 0,
                           book.getImagePaths() != null ? book.getImagePaths().size() : 0);
                
                Book savedBook = bookRepository.save(book);
                logger.info("Book saved successfully with id: {}", savedBook.getId());
                return savedBook;
            } catch (Exception e) {
                logger.error("Error during repository save operation: {}", e.getMessage(), e);
                logger.error("Exception class: {}", e.getClass().getName());
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
                throw new RuntimeException("Errore durante il salvataggio nel database: " + e.getMessage());
            }
            
        } catch (RuntimeException e) {
            // Re-throw runtime exceptions as-is
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in updateBook: {}", e.getMessage(), e);
            // Provide more detailed error information
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "Errore sconosciuto durante l'aggiornamento del libro";
            }
            throw new RuntimeException("Errore nel salvare il libro: " + errorMessage);
        }
    }
    
    public void deleteById(Long id) {
        // Delete associated image files before deleting book
        Optional<Book> book = findById(id);
        if (book.isPresent() && book.get().getImagePaths() != null) {
            for (String imagePath : book.get().getImagePaths()) {
                fileUploadService.deleteFile(imagePath);
            }
        }
        bookRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }
} 