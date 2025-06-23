package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import siwbooks.siwbooks.service.AuthorService;
import siwbooks.siwbooks.model.Author;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/authors")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping
    public String authors(Model model) {
        // Caso d'uso generico: Visualizzazione lista autori
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors/list";
    }
    
    @GetMapping("/{id}")
    public String authorDetail(@PathVariable Long id, Model model) {
        // Caso d'uso generico: Visualizzazione dettagli autore
        Optional<Author> authorOpt = authorService.findById(id);
        if (authorOpt.isEmpty()) {
            return "redirect:/authors";
        }
        
        Author author = authorOpt.get();
        model.addAttribute("author", author);
        
        return "authors/detail";
    }
}