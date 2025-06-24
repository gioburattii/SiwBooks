package siwbooks.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import siwbooks.siwbooks.model.Author;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.repository.AuthorRepository;
import siwbooks.siwbooks.service.IFileStorageService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private IFileStorageService fileStorageService;
    
    @Autowired
    private BookService bookService;
    
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
    
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }
    
    public List<Author> findByName(String name) {
        return authorRepository.findByFullNameContaining(name);
    }
    
    public List<Author> findByNationality(String nationality) {
        return authorRepository.findByNationalityIgnoreCase(nationality);
    }
    
    public Author save(Author author) {
        return authorRepository.save(author);
    }
    
    public Author createAuthor(String firstName, String lastName, LocalDate birthDate, 
                             String nationality, LocalDate deathDate, MultipartFile photoFile) throws IOException {
        Author author = new Author(firstName, lastName, birthDate, nationality);
        author.setDeathDate(deathDate);
        
        // Handle photo upload
        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileStorageService.saveFile(photoFile, "authors");
            author.setPhotoPath(photoPath);
        }
        
        return save(author);
    }
    
    public Author updateAuthor(Long id, String firstName, String lastName, LocalDate birthDate,
                             String nationality, LocalDate deathDate, MultipartFile photoFile) throws IOException {
        Optional<Author> optionalAuthor = findById(id);
        if (optionalAuthor.isEmpty()) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        
        Author author = optionalAuthor.get();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setBirthDate(birthDate);
        author.setNationality(nationality);
        author.setDeathDate(deathDate);
        
        // Handle photo upload
        if (photoFile != null && !photoFile.isEmpty()) {
            // Delete old photo if exists
            if (author.getPhotoPath() != null) {
                fileStorageService.deleteFile(author.getPhotoPath());
            }
            String photoPath = fileStorageService.saveFile(photoFile, "authors");
            author.setPhotoPath(photoPath);
        }
        
        return save(author);
    }
    
    @Transactional
    public void deleteById(Long id) {
        // Find the author
        Optional<Author> authorOpt = findById(id);
        if (authorOpt.isPresent()) {
            Author author = authorOpt.get();
            
            // Force loading of books to handle lazy loading
            Set<Book> authorBooks = author.getBooks();
            if (authorBooks != null) {
                // Force initialization of the lazy collection
                authorBooks.size();
                
                if (!authorBooks.isEmpty()) {
                    // Create a copy of the set to avoid concurrent modification
                    Set<Book> booksToDelete = new HashSet<>(authorBooks);
                    
                    // Delete all books by this author
                    for (Book book : booksToDelete) {
                        bookService.deleteById(book.getId());
                    }
                }
            }
            
            // Delete associated photo file
            if (author.getPhotoPath() != null) {
                fileStorageService.deleteFile(author.getPhotoPath());
            }
        }
        
        // Finally, delete the author
        authorRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return authorRepository.existsById(id);
    }
} 