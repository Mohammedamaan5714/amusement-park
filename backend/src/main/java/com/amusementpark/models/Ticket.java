package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    private String userId;
    private String userName;
    private String email;
    private String phone;
    
    // Map of ticketTypeId to quantity
    private Map<String, Integer> ticketTypes;
    
    // Total number of rides allowed based on ticket types
    private int totalRidesAllowed;
    
    // Booking information
    private LocalDateTime bookingDate;
    private LocalDateTime visitDate;
    
    private double totalPrice;
    private String status; // "BOOKED", "USED", "CANCELLED"
    
    public Ticket() {
        this.bookingDate = LocalDateTime.now();
        this.status = "BOOKED";
    }
}