package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import siwbooks.siwbooks.service.BookService;
import siwbooks.siwbooks.service.ReviewService;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.model.Review;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping
    public String books(Model model) {
        // Caso d'uso generico: Visualizzazione lista libri
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "books/list";
    }
    
    @GetMapping("/{id}")
    public String bookDetail(@PathVariable Long id, Model model) {
        // Caso d'uso generico: Visualizzazione dettagli libro con recensioni
        Optional<Book> bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) {
            return "redirect:/books";
        }
        
        Book book = bookOpt.get();
        List<Review> reviews = reviewService.findByBookId(id);
        
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", reviewService.getAverageRatingForBook(id));
        model.addAttribute("reviewCount", reviewService.getReviewCountForBook(id));
        
        return "books/detail";
    }
} 