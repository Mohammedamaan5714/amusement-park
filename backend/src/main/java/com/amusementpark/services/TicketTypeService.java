package com.amusementpark.services;

import com.amusementpark.models.TicketType;
import com.amusementpark.repositories.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class TicketTypeService {
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    
    /**
     * Get all ticket types
     * @return List of all ticket types
     */
    public List<TicketType> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }
    
    /**
     * Get a ticket type by ID
     * @param id The ticket type ID
     * @return The ticket type
     */
    public TicketType getTicketTypeById(String id) {
        return ticketTypeRepository.findById(id).orElse(null);
    }
    
    /**
     * Initialize ticket types if none exist
     */
    @PostConstruct
    public void initTicketTypes() {
        if (ticketTypeRepository.count() == 0) {
            // Create default ticket types
            TicketType silverTicket = new TicketType(
                "Silver", 
                "Amusement park entry fee with 3 rides", 
                3, 
                299.0, 
                true
            );
            
            TicketType goldTicket = new TicketType(
                "Gold", 
                "Amusement park entry fee with 6 rides", 
                6, 
                499.0, 
                true
            );
            
            TicketType diamondTicket = new TicketType(
                "Diamond", 
                "Amusement park entry fee with 12 rides", 
                12, 
                899.0, 
                true
            );
            
            // Save ticket types
            ticketTypeRepository.save(silverTicket);
            ticketTypeRepository.save(goldTicket);
            ticketTypeRepository.save(diamondTicket);
        }
    }
}