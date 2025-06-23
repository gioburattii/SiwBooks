package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import siwbooks.siwbooks.service.BookService;
import siwbooks.siwbooks.service.AuthorService;
import siwbooks.siwbooks.model.Book;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Caso d'uso 1 (Utente generico): Consultazione homepage con lista libri
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("authors", authorService.findAll());
        return "index";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                        @RequestParam(required = false) String type,
                        Model model) {
        // Caso d'uso 2 (Utente generico): Ricerca libri e autori
        if (query != null && !query.trim().isEmpty()) {
            if ("author".equals(type)) {
                model.addAttribute("authors", authorService.findByName(query));
                model.addAttribute("books", bookService.findByAuthorName(query));
            } else {
                model.addAttribute("books", bookService.findByTitle(query));
                model.addAttribute("authors", authorService.findByName(query));
            }
        } else {
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("authors", authorService.findAll());
        }
        
        model.addAttribute("query", query);
        model.addAttribute("searchType", type);
        return "search-results";
    }
} 