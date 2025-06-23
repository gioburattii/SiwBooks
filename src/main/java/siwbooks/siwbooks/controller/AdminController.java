package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import siwbooks.siwbooks.service.BookService;
import siwbooks.siwbooks.service.AuthorService;
import siwbooks.siwbooks.service.ReviewService;
import siwbooks.siwbooks.service.UserService;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.model.Author;
import siwbooks.siwbooks.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("users", userService.findAll());
        return "admin/dashboard";
    }
    
    // CASO D'USO AMMINISTRATORE 1: Inserimento nuovo libro
    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "admin/books/add";
    }
    
    @PostMapping("/books/add")
    public String addBook(@RequestParam String title,
                         @RequestParam Integer publicationYear,
                         @RequestParam(required = false) List<Long> authorIds,
                         @RequestParam(required = false) List<MultipartFile> imageFiles,
                         RedirectAttributes redirectAttributes) {
        logger.info("Adding new book: title={}, year={}, authorIds={}", 
                   title, publicationYear, authorIds);
        
        if (imageFiles != null) {
            logger.info("Number of image files: {}", imageFiles.size());
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                logger.info("Image file {}: name={}, size={}, contentType={}", 
                           i, file.getOriginalFilename(), file.getSize(), file.getContentType());
            }
        } else {
            logger.info("No image files provided");
        }
        
        try {
            Set<Long> authorIdSet = authorIds != null ? new HashSet<>(authorIds) : new HashSet<>();
            bookService.createBook(title, publicationYear, authorIdSet, imageFiles);
            logger.info("Book created successfully");
            redirectAttributes.addFlashAttribute("success", "Libro aggiunto con successo!");
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiunta del libro: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // CASO D'USO AMMINISTRATORE 2: Modifica libro esistente
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/admin";
        }
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "admin/books/edit";
    }
    
    @PostMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam Integer publicationYear,
                          @RequestParam(required = false) List<Long> authorIds,
                          @RequestParam(required = false) List<MultipartFile> imageFiles,
                          RedirectAttributes redirectAttributes) {
        logger.info("Editing book with id: {}, title: {}, year: {}, authorIds: {}", 
                   id, title, publicationYear, authorIds);
        
        if (imageFiles != null) {
            logger.info("Number of image files: {}", imageFiles.size());
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                logger.info("Image file {}: name={}, size={}, contentType={}", 
                           i, file.getOriginalFilename(), file.getSize(), file.getContentType());
            }
        } else {
            logger.info("No image files provided");
        }
        
        try {
            Set<Long> authorIdSet = authorIds != null ? new HashSet<>(authorIds) : new HashSet<>();
            logger.info("Converting authorIds to Set: {}", authorIdSet);
            
            Book updatedBook = bookService.updateBook(id, title, publicationYear, authorIdSet, imageFiles);
            logger.info("Book updated successfully: {}", updatedBook.getId());
            redirectAttributes.addFlashAttribute("success", "Libro modificato con successo!");
        } catch (Exception e) {
            logger.error("Error updating book with id {}: {}", id, e.getMessage(), e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Errore sconosciuto durante la modifica del libro";
            redirectAttributes.addFlashAttribute("error", "Errore nella modifica del libro: " + errorMessage);
        }
        return "redirect:/admin";
    }
    
    // CASO D'USO AMMINISTRATORE 3: Inserimento nuovo autore
    @GetMapping("/authors/add")
    public String addAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/add";
    }
    
    @PostMapping("/authors/add")
    public String addAuthor(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam LocalDate birthDate,
                           @RequestParam String nationality,
                           @RequestParam(required = false) LocalDate deathDate,
                           @RequestParam(required = false) MultipartFile photoFile,
                           RedirectAttributes redirectAttributes) {
        try {
            authorService.createAuthor(firstName, lastName, birthDate, nationality, deathDate, photoFile);
            redirectAttributes.addFlashAttribute("success", "Autore aggiunto con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiunta dell'autore: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // CASO D'USO AMMINISTRATORE 4: Modifica autore esistente
    @GetMapping("/authors/edit/{id}")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id).orElse(null);
        if (author == null) {
            return "redirect:/admin";
        }
        model.addAttribute("author", author);
        return "admin/authors/edit";
    }
    
    @PostMapping("/authors/edit/{id}")
    public String editAuthor(@PathVariable Long id,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam LocalDate birthDate,
                            @RequestParam String nationality,
                            @RequestParam(required = false) LocalDate deathDate,
                            @RequestParam(required = false) MultipartFile photoFile,
                            RedirectAttributes redirectAttributes) {
        try {
            authorService.updateAuthor(id, firstName, lastName, birthDate, nationality, deathDate, photoFile);
            redirectAttributes.addFlashAttribute("success", "Autore modificato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella modifica dell'autore: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // Cancellazione libri e autori
    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Libro cancellato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella cancellazione del libro: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/authors/delete/{id}")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Autore cancellato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella cancellazione dell'autore: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // Cancellazione recensioni
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Recensione cancellata con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella cancellazione della recensione: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // USER MANAGEMENT - Create Admin User
    @GetMapping("/users/add-admin")
    public String addAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add-admin";
    }
    
    @PostMapping("/users/add-admin")
    public String addAdmin(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.createAdminUser(username, email, password, firstName, lastName);
            redirectAttributes.addFlashAttribute("success", "Amministratore creato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella creazione dell'amministratore: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // USER MANAGEMENT - Update User Role
    @PostMapping("/users/update-role/{id}")
    public String updateUserRole(@PathVariable Long id,
                                @RequestParam User.Role role,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Ruolo utente aggiornato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiornamento del ruolo: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    
    // USER MANAGEMENT - Delete User
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Utente eliminato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'eliminazione dell'utente: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 