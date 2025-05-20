package com.amusementpark.repositories;

import com.amusementpark.models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
    // Custom query methods can be added here
}