package com.amusementpark.controllers;

import com.amusementpark.models.ChatMessage;
import com.amusementpark.services.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ChatService chatService;
    
    /**
     * Receive user message and respond with bot reply
     * @param payload Map containing userId and message
     * @return ResponseEntity with bot reply
     */
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        try {
            String userId = payload.get("userId");
            String message = payload.get("message");
            
            if (userId == null || message == null) {
                logger.warn("Missing required fields in chat request: userId={}, message={}", userId, message);
                return ResponseEntity.badRequest().body("Both userId and message are required");
            }
            
            logger.info("Processing chat message for user: {}", userId);
            ChatMessage botReply = chatService.processUserMessage(userId, message);
            return ResponseEntity.ok(botReply);
        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your message");
        }
    }
    
    /**
     * Get full chat history for a user
     * @param userId The user ID
     * @return ResponseEntity with list of chat messages
     */
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Missing userId in chat history request");
                return ResponseEntity.badRequest().body("UserId is required");
            }
            
            logger.info("Retrieving chat history for user: {}", userId);
            List<ChatMessage> chatHistory = chatService.getChatHistoryByUserId(userId);
            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error retrieving chat history for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving chat history");
        }
    }
    
    /**
     * Get recent 5 messages for a user
     * @param userId The user ID
     * @return ResponseEntity with list of recent chat messages
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentMessages(@RequestParam String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Missing userId in recent messages request");
                return ResponseEntity.badRequest().body("UserId is required");
            }
            
            logger.info("Retrieving recent messages for user: {}", userId);
            List<ChatMessage> allMessages = chatService.getChatHistoryByUserId(userId);
            
            // Get the most recent 5 messages
            List<ChatMessage> recentMessages = allMessages.stream()
                    .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
                    .limit(5)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(recentMessages);
        } catch (Exception e) {
            logger.error("Error retrieving recent messages for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving recent messages");
        }
    }
}