package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private String userId;
    private String message;
    private String sender; // "user" or "bot"
    private LocalDateTime timestamp;
    
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(String userId, String message, String sender) {
        this.userId = userId;
        this.message = message;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }
}