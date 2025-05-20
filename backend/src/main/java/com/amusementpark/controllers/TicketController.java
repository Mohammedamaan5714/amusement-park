package com.amusementpark.controllers;

import com.amusementpark.models.Ticket;
import com.amusementpark.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
        // Calculate total price (entry fee + rides + food)
        double total = ticket.getEntryFee();
        // In a real system, fetch ride prices and food prices from DB
        // Here, assume ride and food prices are included in the request for simplicity
        ticket.setTotalPrice(total);
        ticketRepository.save(ticket);
        return ResponseEntity.ok("Ticket booked successfully");
    }

    @GetMapping("/user/{userId}")
    public List<Ticket> getTicketsByUser(@PathVariable String userId) {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getUserId().equals(userId))
                .toList();
    }
}