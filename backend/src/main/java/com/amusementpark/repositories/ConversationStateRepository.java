package com.amusementpark.repositories;

import com.amusementpark.models.ConversationState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationStateRepository extends MongoRepository<ConversationState, String> {
    
    /**
     * Find conversation state by user ID
     * @param userId The user ID
     * @return The conversation state for the user
     */
    ConversationState findByUserId(String userId);
}