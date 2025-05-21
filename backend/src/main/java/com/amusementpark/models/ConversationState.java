package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.ArrayList; // Added import
import java.util.List;    // Added import

@Data
@Document(collection = "conversation_states")
public class ConversationState {
    @Id
    private String id;
    private String userId;
    private String currentTopic; // Current conversation topic (e.g., "tickets", "rides", "food")
    private Map<String, Object> contextData; // Stores conversation context data
    private LocalDateTime lastUpdated;
    private int conversationStep; // Tracks the step in a multi-turn conversation
    private List<String> history; // Added field for chat history
    private long lastInteractionTime; // Added field for last interaction timestamp (milliseconds)

    public ConversationState() {
        this.contextData = new HashMap<>();
        this.lastUpdated = LocalDateTime.now();
        this.conversationStep = 0;
        this.history = new ArrayList<>(); // Initialize history
    }
    
    public ConversationState(String userId) {
        this.userId = userId;
        this.contextData = new HashMap<>();
        this.lastUpdated = LocalDateTime.now();
        this.conversationStep = 0;
        this.history = new ArrayList<>(); // Initialize history
    }
    
    /**
     * Store a value in the context data
     * @param key The context key
     * @param value The context value
     */
    public void setContextValue(String key, Object value) {
        this.contextData.put(key, value);
    }
    
    /**
     * Get a value from the context data
     * @param key The context key
     * @return The context value
     */
    public Object getContextValue(String key) {
        return this.contextData.get(key);
    }
    
    /**
     * Check if a key exists in the context data
     * @param key The context key
     * @return True if the key exists, false otherwise
     */
    public boolean hasContextValue(String key) {
        return this.contextData.containsKey(key);
    }
    
    /**
     * Increment the conversation step
     * @return The new step value
     */
    public int incrementStep() {
        this.conversationStep++;
        return this.conversationStep;
    }
    
    /**
     * Reset the conversation state
     */
    public void reset() {
        this.currentTopic = null;
        this.contextData.clear();
        this.conversationStep = 0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Update the last updated timestamp
     */
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Adds a message to the conversation history.
     * The history list is initialized in the constructors.
     * @param message The message to add.
     */
    public void addToHistory(String message) {
        if (this.history == null) { // Defensive check, constructors should initialize.
            this.history = new ArrayList<>();
        }
        this.history.add(message);
    }

    // Note: Lombok's @Data annotation will automatically generate getters and setters
    // for the new fields 'history' (including getHistory()) and 'lastInteractionTime'
    // (including setLastInteractionTime(long) and getLastInteractionTime()).
}