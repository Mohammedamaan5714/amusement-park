package com.amusementpark.controllers;

import com.amusementpark.models.Ticket;
import com.amusementpark.models.TicketType;
import com.amusementpark.services.TicketService;
import com.amusementpark.services.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketBookingController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketTypeService ticketTypeService;
    
    /**
     * Get all available ticket types
     * @return List of ticket types
     */
    @GetMapping("/types")
    public ResponseEntity<List<TicketType>> getTicketTypes() {
        return ResponseEntity.ok(ticketTypeService.getAllTicketTypes());
    }
    
    /**
     * Book tickets
     * @param ticket The ticket booking request
     * @return The booked ticket
     */
    @PostMapping("/book")
    public ResponseEntity<Ticket> bookTicket(@RequestBody Ticket ticket) {
        Ticket bookedTicket = ticketService.bookTicket(ticket);
        return ResponseEntity.ok(bookedTicket);
    }
    
    /**
     * Get tickets by user ID
     * @param userId The user ID
     * @return List of tickets for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }
    
    /**
     * Get ticket by ID
     * @param id The ticket ID
     * @return The ticket
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get ticket information for the frontend
     * @return Map containing ticket types and pricing information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getTicketInfo() {
        Map<String, Object> ticketInfo = new HashMap<>();
        
        // Add ticket types
        ticketInfo.put("ticketTypes", ticketTypeService.getAllTicketTypes());
        
        // Add park timings
        Map<String, String> parkTimings = new HashMap<>();
        parkTimings.put("summer", "11:00 AM - 7:00 PM");
        ticketInfo.put("parkTimings", parkTimings);
        
        // Add important notes
        List<String> importantNotes = List.of(
            "Park entry ticket prices are subject to change without prior notice.",
            "Park timings are subject to change without prior notice."
        );
        ticketInfo.put("importantNotes", importantNotes);
        
        return ResponseEntity.ok(ticketInfo);
    }
}