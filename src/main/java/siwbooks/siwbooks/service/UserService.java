package siwbooks.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import siwbooks.siwbooks.model.User;
import siwbooks.siwbooks.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        // Encode password before saving
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    public User registerUser(String username, String email, String password, 
                           String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, password, firstName, lastName);
        user.setRole(User.Role.USER);
        return save(user);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Method to create admin users (only for existing admins)
    public User createAdminUser(String username, String email, String password, 
                               String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, password, firstName, lastName);
        user.setRole(User.Role.ADMIN);
        return save(user);
    }
    
    // Method to update user role
    public User updateUserRole(Long userId, User.Role newRole) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        User user = userOpt.get();
        user.setRole(newRole);
        // Don't re-encode password when just updating role
        return userRepository.save(user);
    }
    
    // Method to delete user
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    // Method to find users by role
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
} 