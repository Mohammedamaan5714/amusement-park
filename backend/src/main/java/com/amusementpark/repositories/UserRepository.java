package com.amusementpark.repositories;

import com.amusementpark.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for User entity operations
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find a user by username
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    User findByUsername(String username);
    
    /**
     * Find a user by email
     * @param email The email to search for
     * @return The user if found, null otherwise
     */
    User findByEmail(String email);
}