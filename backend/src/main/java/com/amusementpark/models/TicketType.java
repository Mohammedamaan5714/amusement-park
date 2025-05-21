package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ticket_types")
public class TicketType {
    @Id
    private String id;
    private String name;
    private String description;
    private int rideLimit;
    private double price;
    private boolean freeForChildren; // Free for children below 2.5 ft
    
    // Default constructor
    public TicketType() {}
    
    // Constructor with all fields
    public TicketType(String name, String description, int rideLimit, double price, boolean freeForChildren) {
        this.name = name;
        this.description = description;
        this.rideLimit = rideLimit;
        this.price = price;
        this.freeForChildren = freeForChildren;
    }
}