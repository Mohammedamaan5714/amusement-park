package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rides")
public class Ride {
    @Id
    private String id;
    private String name;
    private String description;
    private String category; // "THRILL", "FAMILY", "KIDS", "THEMED"
    private String imageUrl;
    private boolean isActive;
    
    // Default constructor
    public Ride() {
        this.isActive = true;
    }
    
    // Constructor with fields
    public Ride(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.isActive = true;
    }
}