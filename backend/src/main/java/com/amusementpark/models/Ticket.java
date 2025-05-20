package com.amusementpark.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    private String userId;
    private List<String> rideIds;
    private List<String> foodItems;
    private double entryFee;
    private double totalPrice;
    // Add more fields as needed
}