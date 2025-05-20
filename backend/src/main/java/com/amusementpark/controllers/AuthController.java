package com.amusementpark.controllers;

import com.amusementpark.models.User;
import com.amusementpark.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling user authentication operations
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Register a new user
     * @param userData User registration data
     * @return ResponseEntity with registration result
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User userData) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(userData.getUsername()) != null) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(userData.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Email already exists");
            }
            
            // In a real application, you would hash the password here
            // For simplicity, we're storing it as plain text (NOT recommended for production)
            
            // Create new user
            User newUser = new User(userData.getUsername(), userData.getEmail(), userData.getPassword());
            userRepository.save(newUser);
            
            logger.info("New user registered: {}", userData.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration");
        }
    }
    
    /**
     * Login a user
     * @param credentials User login credentials
     * @param session HTTP session
     * @return ResponseEntity with login result
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials, HttpSession session) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
            
            // Find user by username
            User user = userRepository.findByUsername(username);
            
            // Check if user exists and password matches
            if (user == null || !user.getPassword().equals(password)) {
                // In a real application, you would use a password encoder to compare passwords
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
            
            // Store user in session
            session.setAttribute("user", user);
            
            logger.info("User logged in: {}", username);
            
            // Return user data (excluding password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during login");
        }
    }
    
    /**
     * Logout a user
     * @param request HTTP request
     * @return ResponseEntity with logout result
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            logger.info("User logged out");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during logout");
        }
    }
    
    /**
     * Get current user information
     * @param session HTTP session
     * @return ResponseEntity with user data
     */
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }
            
            // Return user data (excluding password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user data");
        }
    }
}