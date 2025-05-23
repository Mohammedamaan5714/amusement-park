package com.amusementpark.repositories;

import com.amusementpark.models.TicketType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends MongoRepository<TicketType, String> {
    // Add custom query methods if needed
}