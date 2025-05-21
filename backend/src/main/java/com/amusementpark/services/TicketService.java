package com.amusementpark.services;

import com.amusementpark.models.Ticket;
import com.amusementpark.models.TicketType;
import com.amusementpark.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketTypeService ticketTypeService;
    
    /**
     * Book a new ticket
     * @param ticket The ticket to book
     * @return The booked ticket
     */
    public Ticket bookTicket(Ticket ticket) {
        // Calculate total rides allowed and total price
        int totalRidesAllowed = 0;
        double totalPrice = 0.0;
        
        for (Map.Entry<String, Integer> entry : ticket.getTicketTypes().entrySet()) {
            String ticketTypeId = entry.getKey();
            Integer quantity = entry.getValue();
            
            TicketType ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
            if (ticketType != null) {
                totalRidesAllowed += ticketType.getRideLimit() * quantity;
                totalPrice += ticketType.getPrice() * quantity;
            }
        }
        
        ticket.setTotalRidesAllowed(totalRidesAllowed);
        ticket.setTotalPrice(totalPrice);
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Get tickets by user ID
     * @param userId The user ID
     * @return List of tickets for the user
     */
    public List<Ticket> getTicketsByUserId(String userId) {
        return ticketRepository.findByUserId(userId);
    }
    
    /**
     * Get a ticket by ID
     * @param id The ticket ID
     * @return The ticket
     */
    public Ticket getTicketById(String id) {
        return ticketRepository.findById(id).orElse(null);
    }
}