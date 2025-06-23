package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import siwbooks.siwbooks.service.ReviewService;
import siwbooks.siwbooks.service.BookService;
import siwbooks.siwbooks.service.UserService;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.model.User;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    // CASO D'USO UTENTE REGISTRATO 1: Aggiunta recensione
    @GetMapping("/add/{bookId}")
    public String addReviewForm(@PathVariable Long bookId, Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Verifica che l'utente sia di tipo USER (non ADMIN)
        if (user.getRole() != User.Role.USER) {
            return "redirect:/books/" + bookId + "?error=admin_cannot_review";
        }
        
        Book book = bookService.findById(bookId).orElse(null);
        if (book == null) {
            return "redirect:/books";
        }
        
        // Verifica se l'utente ha già recensito questo libro
        if (reviewService.hasUserReviewedBook(bookId, user.getId())) {
            return "redirect:/books/" + bookId + "?error=already_reviewed";
        }
        
        model.addAttribute("book", book);
        return "reviews/add";
    }
    
    @PostMapping("/add/{bookId}")
    public String addReview(@PathVariable Long bookId,
                           @RequestParam String title,
                           @RequestParam Integer rating,
                           @RequestParam String content,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        try {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            if (user == null) {
                return "redirect:/login";
            }
            
            // Verifica che l'utente sia di tipo USER (non ADMIN)
            if (user.getRole() != User.Role.USER) {
                redirectAttributes.addFlashAttribute("error", "Gli amministratori non possono lasciare recensioni!");
                return "redirect:/books/" + bookId;
            }
            
            reviewService.createReview(title, rating, content, bookId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Recensione aggiunta con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiunta della recensione: " + e.getMessage());
        }
        
        return "redirect:/books/" + bookId;
    }
    
    // CASO D'USO UTENTE REGISTRATO 2: Visualizzazione proprie recensioni
    @GetMapping("/my-reviews")
    public String myReviews(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Verifica che l'utente sia di tipo USER (non ADMIN)
        if (user.getRole() != User.Role.USER) {
            return "redirect:/?error=admin_no_reviews";
        }
        
        model.addAttribute("reviews", reviewService.findByUserId(user.getId()));
        model.addAttribute("user", user);
        return "reviews/my-reviews";
    }
    
    // Cancellazione recensione (solo per l'utente che l'ha scritta)
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        try {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            if (user == null) {
                return "redirect:/login";
            }
            
            var review = reviewService.findById(id);
            if (review.isPresent() && review.get().getUser().getId().equals(user.getId())) {
                reviewService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Recensione cancellata con successo!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Non puoi cancellare questa recensione!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella cancellazione della recensione: " + e.getMessage());
        }
        
        return "redirect:/reviews/my-reviews";
    }
} 