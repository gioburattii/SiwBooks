package siwbooks.siwbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import siwbooks.siwbooks.service.UserService;
import siwbooks.siwbooks.model.User;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model, 
                       java.security.Principal principal) {
        // Se l'utente è già autenticato, reindirizza alla homepage
        if (principal != null) {
            return "redirect:/";
        }
        
        if (error != null) {
            model.addAttribute("error", "Username o password non validi");
        }
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model, java.security.Principal principal) {
        // Se l'utente è già autenticato, reindirizza alla homepage
        if (principal != null) {
            return "redirect:/";
        }
        
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, email, password, firstName, lastName);
            redirectAttributes.addFlashAttribute("success", "Registrazione completata! Ora puoi effettuare il login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella registrazione: " + e.getMessage());
            return "redirect:/register";
        }
    }
} 