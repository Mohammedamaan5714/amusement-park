package com.amusementpark.services;

import com.amusementpark.models.ChatMessage;
import com.amusementpark.models.Ride;
import com.amusementpark.models.TicketType;
import com.amusementpark.repositories.ChatMessageRepository;
import com.amusementpark.repositories.ConversationStateRepository; // Added import
import com.amusementpark.models.ConversationState; // Added import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ConversationStateRepository conversationStateRepository; // Added repository
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RideService rideService; // Added RideService dependency

    @Autowired
    private TicketTypeService ticketTypeService; // Added TicketTypeService dependency

    // Updated constructor to include ConversationStateRepository
    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository, RideService rideService, TicketTypeService ticketTypeService, ConversationStateRepository conversationStateRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.rideService = rideService;
        this.ticketTypeService = ticketTypeService;
        this.conversationStateRepository = conversationStateRepository;
    }
    
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
    public ChatMessage processUserMessage(String userId, String userMessageText) {
        // 1. Save user message
        ChatMessage userMessage = new ChatMessage(userId, userMessageText, "user");
        chatMessageRepository.save(userMessage);

        // 2. Retrieve or create conversation state
        ConversationState state = conversationStateRepository.findByUserId(userId);
        if (state == null) {
            state = new ConversationState(userId);
        }

        // 3. Update conversation state with user's message
        state.addToHistory("User: " + userMessageText);
        state.setLastInteractionTime(System.currentTimeMillis());
        // Example: Storing the raw user message for context
        state.setContextValue("last_user_message_text", userMessageText);
        conversationStateRepository.save(state);

        // 4. Generate bot reply using conversation state
        String botReplyText = generateBotReply(state, userMessageText); // Pass state and original message

        // 5. Save bot message
        ChatMessage botMessage = new ChatMessage(userId, botReplyText, "bot");
        chatMessageRepository.save(botMessage);

        // 6. Update conversation state with bot's message
        state.addToHistory("Bot: " + botReplyText);
        state.setLastInteractionTime(System.currentTimeMillis());
        conversationStateRepository.save(state);

        return botMessage;
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
     * @param state The current conversation state
     * @param userMessageText The original user's message text (can be used if needed, or rely on state.getContextValue("last_user_message_text"))
     * @return The bot's reply
     */
    public String generateBotReply(ConversationState state, String userMessageText) {
        String msg = userMessageText.toLowerCase();
        String lastIntent = (String) state.getContextValue("last_intent");
        String groupType = (String) state.getContextValue("groupType");

        // --- Enhanced Greetings ---
        if (msg.matches(".*\\b(hello|hi|hey|good morning|good evening)\\b.*") && (lastIntent == null || !lastIntent.startsWith("await_"))) {
            state.reset(); // Reset state on new greeting if not in an active flow
            state.setContextValue("last_intent", "greet");
            return "Hey there! 😊 Welcome to Amusement Park Chat! You can ask about 🎢 rides, 🎟️ tickets, 🍔 food stalls, or park 🕒 timings.";
        }

        // --- Enhanced Ticket Intent Recognition ---
        boolean isNewTicketKeyword = msg.matches(".*\\b(ticket|buy|purchase|entry|pass|booking)\\b.*");

        if (isNewTicketKeyword) { // If user mentions ticket keyword, we re-evaluate the ticket intent start.
            state.setContextValue("last_intent", "buy_ticket");
            lastIntent = "buy_ticket"; // Reset to the beginning of ticket flow

            // Check if the current message *also* specifies a group type
            if (msg.contains("friend")) {
                state.setContextValue("groupType", "friends");
                groupType = "friends";
                state.setContextValue("last_intent", "await_friends_adult_status_confirmation");
                lastIntent = "await_friends_adult_status_confirmation";
            } else if (msg.contains("family")) {
                state.setContextValue("groupType", "family");
                groupType = "family";
                state.setContextValue("last_intent", "await_family_composition");
                lastIntent = "await_family_composition";
            } else if (msg.contains("alone") || msg.contains("myself") || msg.contains("just me") || msg.contains("solo")) {
                state.setContextValue("groupType", "solo");
                groupType = "solo";
                state.setContextValue("adults", 1);
                state.setContextValue("children", 0);
                state.setContextValue("last_intent", "suggest_solo_ticket_options");
                lastIntent = "suggest_solo_ticket_options";
            } else {
                // Ticket keyword mentioned, but no group type in this specific message.
                // Clear previous groupType so it's asked again.
                state.setContextValue("groupType", null);
                groupType = null;
                // lastIntent remains "buy_ticket", which will lead to asking group type.
            }
        }

        boolean inTicketFlow = lastIntent != null && 
                               (lastIntent.equals("buy_ticket") || 
                                lastIntent.startsWith("await_") || 
                                lastIntent.equals("group_details") || 
                                lastIntent.equals("suggest_solo_ticket_options") ||
                                lastIntent.equals("group_details_updated"));

        if (inTicketFlow) {
            // 1. Determine Group Type if not set or clarification needed
            if (groupType == null || "await_group_type_clarification".equals(lastIntent)) {
                if (msg.contains("friend")) {
                    state.setContextValue("groupType", "friends");
                    groupType = "friends";
                    state.setContextValue("last_intent", "await_friends_adult_status_confirmation");
                    lastIntent = "await_friends_adult_status_confirmation";
                    return "Are all your friends adults?";
                } else if (msg.contains("family")) {
                    state.setContextValue("groupType", "family");
                    groupType = "family";
                    state.setContextValue("last_intent", "await_family_composition");
                    lastIntent = "await_family_composition";
                    return "How many adults and how many children will be joining?";
                } else if (msg.contains("alone") || msg.contains("myself") || msg.contains("just me") || msg.contains("solo")) {
                    state.setContextValue("groupType", "solo");
                    groupType = "solo";
                    state.setContextValue("adults", 1);
                    state.setContextValue("children", 0);
                    state.setContextValue("last_intent", "suggest_solo_ticket_options");
                    lastIntent = "suggest_solo_ticket_options";
                    return "You can choose from Silver, Gold, or Diamond passes. Want help deciding?";
                } else { // If the current message didn't clarify group type or it's a fresh "buy_ticket" intent
                    state.setContextValue("last_intent", "await_group_type_clarification");
                    return "Are you visiting alone, with friends, or with family?";
                }
            }

            // 2. Process based on groupType and lastIntent
            if ("friends".equals(groupType)) {
                if ("await_friends_adult_status_confirmation".equals(lastIntent)) {
                    if (msg.contains("yes") || msg.contains("yeah") || msg.contains("yep")) {
                        state.setContextValue("friends_are_adults", true);
                        state.setContextValue("last_intent", "await_friends_count");
                        return "Great! How many friends will be joining you?";
                    } else if (msg.contains("no") || msg.contains("nope")) {
                        state.setContextValue("friends_are_adults", false);
                        state.setContextValue("last_intent", "await_friends_mixed_composition");
                        return "Okay. Please tell me how many adults and how many children are in your group of friends.";
                    } else {
                        return "Sorry, I didn't catch that. Are all your friends adults? (yes/no)";
                    }
                } else if ("await_friends_count".equals(lastIntent)) {
                    int numberOfFriends = extractNumberFromText(msg, "");
                    if (numberOfFriends > 0) {
                        state.setContextValue("adults", numberOfFriends + 1); // User + friends
                        state.setContextValue("children", 0);
                        state.setContextValue("last_intent", "group_details_updated");
                        return suggestTicketsAndRides(state);
                    } else {
                        return "Please tell me the number of friends joining you.";
                    }
                } else if ("await_friends_mixed_composition".equals(lastIntent)) {
                    int adultsInFriendsGroup = extractNumberFromText(msg, "adult");
                    int childrenInFriendsGroup = extractNumberFromText(msg, "child");
                    if (adultsInFriendsGroup == 0 && childrenInFriendsGroup == 0 && msg.matches(".*\\d+.*")) { // If no keywords but numbers exist
                        // Try to parse two numbers or one, this is a simplification
                        // A more robust parser would be needed for "2 adults 1 child" vs "2 and 1"
                        // For now, let's assume if one number, it's adults, if two, first is adults, second children
                        // This is a heuristic and might need refinement.
                        int[] numbers = extractMultipleNumbers(msg, 2);
                        if (numbers.length >= 1) adultsInFriendsGroup = numbers[0];
                        if (numbers.length >= 2) childrenInFriendsGroup = numbers[1];
                    }

                    if (adultsInFriendsGroup > 0 || childrenInFriendsGroup > 0) {
                        state.setContextValue("adults", adultsInFriendsGroup + 1); // +1 for the user
                        state.setContextValue("children", childrenInFriendsGroup);
                        if (childrenInFriendsGroup > 0) {
                            state.setContextValue("last_intent", "await_children_height_confirmation");
                            return "Are the children in your group shorter than 2.5 ft in height?";
                        } else {
                            state.setContextValue("last_intent", "group_details_updated");
                            return suggestTicketsAndRides(state);
                        }
                    } else {
                        return "Please specify the number of adults and children among your friends.";
                    }
                }
            } else if ("family".equals(groupType)) {
                if ("await_family_composition".equals(lastIntent)) {
                    int adults = extractNumberFromText(msg, "adult");
                    int children = extractNumberFromText(msg, "child");
                    if (adults == 0 && children == 0 && msg.matches(".*\\d+.*")) {
                        int[] numbers = extractMultipleNumbers(msg, 2);
                        if (numbers.length >= 1) adults = numbers[0]; // Assume first number is adults if not specified
                        if (numbers.length >= 2) children = numbers[1];
                    }

                    if (adults > 0 || children > 0) {
                        state.setContextValue("adults", adults);
                        state.setContextValue("children", children);
                        if (children > 0) {
                            state.setContextValue("last_intent", "await_children_height_confirmation");
                            return "Are the children shorter than 2.5 ft in height?";
                        } else {
                            state.setContextValue("last_intent", "group_details_updated");
                            return suggestTicketsAndRides(state);
                        }
                    } else {
                        return "Please tell me how many adults and children will be joining.";
                    }
                } else if ("await_children_height_confirmation".equals(lastIntent)) {
                    if (msg.contains("yes") || msg.contains("yeah") || msg.contains("yep")) {
                        state.setContextValue("childrenFree", true);
                    } else if (msg.contains("no") || msg.contains("nope")) {
                        state.setContextValue("childrenFree", false);
                    } else {
                        return "Sorry, I didn't catch that. Are the children shorter than 2.5 ft? (yes/no)";
                    }
                    state.setContextValue("last_intent", "group_details_updated");
                    return suggestTicketsAndRides(state);
                }
            } else if ("solo".equals(groupType)) {
                if ("suggest_solo_ticket_options".equals(lastIntent)) {
                    if (msg.contains("yes") || msg.contains("help") || msg.contains("decide") || msg.matches(".*\\b(silver|gold|diamond)\\b.*")) {
                        if(msg.matches(".*\\b(silver|gold|diamond)\\b.*")){
                            state.setContextValue("preferredPass", msg.replaceAll(".*\\b(silver|gold|diamond)\\b.*", "$1"));
                        }
                        return suggestTicketsAndRides(state);
                    } else {
                         // If user says something else, or just 'no' to help, still suggest general options
                        return suggestTicketsAndRides(state);
                    }
                }
                 // If groupType is solo and adults/children already set, can directly suggest
                return suggestTicketsAndRides(state);
            }
            // If in ticket flow but no specific sub-handler matched, could be an unexpected response
            // The specific handlers should provide re-prompts. If we reach here, it's a gap or user changed topic.
        }

        // --- Enhanced Ride Info Intent --- (Allow breaking out of ticket flow if specifically asked)
        if (msg.matches(".*\\b(ride|rides|rollercoaster|attraction|thrill)\\b.*")) {
            List<Ride> rides = rideService.getAllRides();
            return rides.isEmpty() ? "Sorry, we currently don't have ride info." : summarizeRides(rides);
        }

        // --- Food Options ---
        if (msg.matches(".*\\b(food|eat|restaurant|snack|lunch|dinner|meal)\\b.*")) {
            return "Hungry? 🍕 We have stalls like 'Pizza Paradise', 'Park Diner', and 'Spicy Grill'. Veg & non-veg options available!";
        }

        // --- Time Info ---
        if (msg.matches(".*\\b(time|timing|open|closing|hours)\\b.*")) {
            return "⏰ We're open daily from 11:00 AM to 7:00 PM during the summer season!";
        }

        // --- Polite Ending ---
        if (msg.contains("thanks") || msg.contains("thank you")) {
            if ("ticket_suggestion_provided".equals(lastIntent) || "greet".equals(lastIntent)) {
                 state.reset(); // Reset if conversation segment concluded
            }
            return "You're welcome! 😊 Anything else you’d like to know?";
        }

        if (msg.contains("bye") || msg.contains("goodbye") || msg.contains("see you")) {
            state.reset();
            return "Goodbye! 🎉 Hope you have an amazing day at the park!";
        }

        // --- Fallback Intent --- 
        // If still in an await state, the specific handler should have re-prompted.
        // This is a more generic fallback if no other intent matched.
        if (lastIntent != null && lastIntent.startsWith("await_")) {
            // This means the user's response was not understood in the context of the current question.
            // Re-prompt based on the question implied by lastIntent.
            // Example: if lastIntent was await_friends_count, re-prompt for number of friends.
            // This is a simplified fallback; ideally, each await state has its specific re-prompt.
            if ("await_group_type_clarification".equals(lastIntent)) return "Are you visiting alone, with friends, or with family?";
            if ("await_friends_adult_status_confirmation".equals(lastIntent)) return "Are all your friends adults? (yes/no)";
            if ("await_friends_count".equals(lastIntent)) return "Please tell me the number of friends joining you.";
            if ("await_family_composition".equals(lastIntent)) return "How many adults and how many children will be joining?";
            if ("await_children_height_confirmation".equals(lastIntent)) return "Are the children shorter than 2.5 ft? (yes/no)";
            // Add more specific re-prompts if needed
        }

        return "Hmm 🤔 I didn't quite get that. You can ask about 🎢 rides, 🎟️ tickets, 🍔 food stalls, or park 🕒 timings.";
    }

    // Helper method to extract multiple numbers, very basic
    private int[] extractMultipleNumbers(String text, int maxNumbers) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        java.util.List<Integer> numbers = new java.util.ArrayList<>();
        while (matcher.find() && numbers.size() < maxNumbers) {
            numbers.add(Integer.parseInt(matcher.group()));
        }
        return numbers.stream().mapToInt(i -> i).toArray();
    }

    private int extractNumberFromText(String msg, String keyword) {
        msg = msg.toLowerCase();
        if (msg.contains(keyword)) {
            String[] words = msg.split("\\s+"); // Split by any whitespace
            for (int i = 0; i < words.length; i++) {
                // Check if the keyword (or its plural) is present and a number precedes it
                if ((words[i].startsWith(keyword)) && i > 0) {
                    try {
                        return Integer.parseInt(words[i - 1].replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException ignored) {}
                }
                // Check if a number is followed by the keyword
                if (i + 1 < words.length && words[i+1].startsWith(keyword)) {
                     try {
                        return Integer.parseInt(words[i].replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        // Fallback: if no keyword, try to extract first number if context implies quantity
        // This is a simplistic fallback, real NLP would be better.
        if (keyword.isEmpty()) { 
            String[] words = msg.split("\\s+");
            for (String word : words) {
                try {
                    return Integer.parseInt(word.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0; // default
    }

    private String summarizeRides(List<Ride> rides) {
        if (rides == null || rides.isEmpty()) {
            return "We currently don't have information on rides. Please check back later!";
        }
        String rideDetails = rides.stream()
            .collect(Collectors.groupingBy(Ride::getCategory))
            .entrySet().stream()
            .map(entry -> {
                String categoryName = entry.getKey();
                // Basic capitalization for category
                if (categoryName != null && !categoryName.isEmpty()) {
                    categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase();
                }
                String rideNames = entry.getValue().stream().map(Ride::getName).collect(Collectors.joining(", "));
                return String.format("%s Rides: %s.", categoryName, rideNames);
            })
            .collect(Collectors.joining("\n"));
        return "We have " + rides.size() + " exciting rides at our amusement park across several categories:\n\n" + rideDetails;
    }

    private String suggestTicketsAndRides(ConversationState context) {
        Object adultsObj = context.getContextValue("adults");
        Object childrenObj = context.getContextValue("children");
        Object childrenFreeObj = context.getContextValue("childrenFree");

        int adults = (adultsObj instanceof Integer) ? (Integer) adultsObj : 0;
        int children = (childrenObj instanceof Integer) ? (Integer) childrenObj : 0;
        boolean childrenFree = (childrenFreeObj instanceof Boolean) ? (Boolean) childrenFreeObj : false;
        
        // If adults or children count is 0 from extractNumberFromText, try to get a general number if not specific
        if (adults == 0 && children == 0) {
            String lastUserMessage = (String) context.getContextValue("last_user_message_text");
            if (lastUserMessage != null) {
                int extractedNum = extractNumberFromText(lastUserMessage, ""); // Try to get any number
                if (extractedNum > 0) {
                    // Heuristic: if group type is family and one number, assume adults or total
                    if ("family".equals(context.getContextValue("groupType"))) {
                        adults = extractedNum; // Default to adults if not specified
                    } else if ("friends".equals(context.getContextValue("groupType"))) {
                        adults = extractedNum;
                    }
                }
            }
        }
        if (adults == 0 && "solo".equals(context.getContextValue("groupType"))) adults = 1;


        StringBuilder sb = new StringBuilder();
        sb.append("Great! Based on your information");

        if (adults > 0) {
            sb.append("- For ").append(adults).append(" adult(s), we recommend Gold Tickets.");
        }
        if (children > 0) {
            if (childrenFree) {
                sb.append("- ").append(children).append(" child(ren) (below 2.5 ft) can enter for free!");
            } else {
                sb.append("- For ").append(children).append(" child(ren), we recommend Diamond Tickets.");
            }
        }
        if (adults == 0 && children == 0 && "solo".equals(context.getContextValue("groupType"))) {
             sb.append("- For a solo visitor, you might like our Silver, Gold or Diamond ticket. Gold is popular!");
        } else if (adults == 0 && children == 0 && context.getContextValue("groupType") == null){
            sb.append("I can help you with tickets. How many people are in your group?");
            return sb.toString();
        }


        // Fetch all ticket types to give price examples
        List<TicketType> allTicketTypes = ticketTypeService.getAllTicketTypes();
        if (!allTicketTypes.isEmpty()) {
            sb.append("\nTicket Price Examples (per person):");
            allTicketTypes.stream()
                .filter(tt -> tt.getName().equalsIgnoreCase("Gold") || tt.getName().equalsIgnoreCase("Diamond") || tt.getName().equalsIgnoreCase("Silver"))
                .forEach(tt -> sb.append(String.format("- %s Ticket: Rs %.2f\n", tt.getName(), tt.getPrice())));
        }

        sb.append("\n🎢 Recommended rides for adults: Thunder Coaster, Sky Drop\n");
        sb.append("🎠 For children: Mini Carousel, Water Splash\n");
        sb.append("🍔 Food stalls nearby: Burger Bonanza, Candy Corner\n");
        sb.append("\nWould you like to proceed with a booking or ask something else?");

        context.setContextValue("last_intent", "ticket_suggestion_provided"); // Update intent
        return sb.toString();
    }

}