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
    private double price;
    // Add more fields as needed
}