package siwbooks.siwbooks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import siwbooks.siwbooks.model.User;
import siwbooks.siwbooks.service.UserService;
import siwbooks.siwbooks.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Crea solo un utente amministratore se non esiste già
        if (!userService.existsByUsername("admin")) {
            User admin = userService.registerUser("admin", "admin@siwbooks.com", "password", "Admin", "Sistema");
            admin.setRole(User.Role.ADMIN);
            // Usa il repository direttamente per evitare la doppia codifica della password
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=password");
        }
    }
} 