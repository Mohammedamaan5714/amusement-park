package com.amusementpark.services;

import com.amusementpark.models.ChatMessage;
import com.amusementpark.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    /**
     * Save a chat message to the database
     * @param chatMessage The message to save
     * @return The saved message
     */
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
    
    /**
     * Save a user message and generate a bot reply
     * @param userId The user ID
     * @param userMessage The user's message
     * @return The bot's reply message
     */
    public ChatMessage processUserMessage(String userId, String userMessage) {
        // Save user message
        ChatMessage userChatMessage = new ChatMessage(userId, userMessage, "user");
        chatMessageRepository.save(userChatMessage);
        
        // Generate and save bot reply
        String botReply = generateBotReply(userMessage);
        ChatMessage botChatMessage = new ChatMessage(userId, botReply, "bot");
        return chatMessageRepository.save(botChatMessage);
    }
    
    /**
     * Retrieve chat history for a specific user
     * @param userId The user ID
     * @return List of chat messages for the user
     */
    public List<ChatMessage> getChatHistoryByUserId(String userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
    }
    
    /**
     * Generate a bot reply based on keywords in the user message
     * @param userMessage The user's message
     * @return The bot's reply
     */
    public String generateBotReply(String userMessage) {
        String lowerCaseMessage = userMessage.toLowerCase();
        
        if (lowerCaseMessage.contains("ride") || lowerCaseMessage.contains("rides") || lowerCaseMessage.contains("attraction")) {
            return "We have over 20 exciting rides at our amusement park, including roller coasters, water rides, and family-friendly attractions. Our most popular rides are 'The Thunderbolt', 'Splash Mountain', and 'Fantasy Carousel'.";
        } else if (lowerCaseMessage.contains("ticket") || lowerCaseMessage.contains("tickets") || lowerCaseMessage.contains("price") || lowerCaseMessage.contains("cost")) {
            return "Our ticket prices are: Adults (12+): $45, Children (3-11): $35, Seniors (65+): $30. We also offer family packages and season passes. You can book tickets online or at the entrance.";
        } else if (lowerCaseMessage.contains("food") || lowerCaseMessage.contains("restaurant") || lowerCaseMessage.contains("eat") || lowerCaseMessage.contains("drink")) {
            return "We have multiple food options throughout the park, including restaurants, food carts, and snack bars. Our main restaurants are 'Park Diner', 'Pizza Paradise', and 'Burger Bonanza'. We accommodate various dietary restrictions.";
        } else if (lowerCaseMessage.contains("hour") || lowerCaseMessage.contains("time") || lowerCaseMessage.contains("open") || lowerCaseMessage.contains("close")) {
            return "Our park is open from 9:00 AM to 8:00 PM Monday through Thursday, and 9:00 AM to 10:00 PM on Fridays, Saturdays, and Sundays. Special holiday hours may apply.";
        } else if (lowerCaseMessage.contains("hello") || lowerCaseMessage.contains("hi") || lowerCaseMessage.contains("hey")) {
            return "Hello! Welcome to Amusement Park Chat. How can I help you today? You can ask about rides, tickets, food, or park hours.";
        } else {
            return "I'm not sure I understand. You can ask me about our rides, ticket prices, food options, or park hours. How can I assist you today?";
        }
    }
}