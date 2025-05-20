package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User entity for authentication and user management
 */
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    private String password; // This would be stored hashed in a real application
    
    @Indexed(unique = true)
    private String email;
    
    private String role; // For basic role-based access control
    
    public User() {
        // Default constructor required by MongoDB
    }
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "USER"; // Default role
    }
}