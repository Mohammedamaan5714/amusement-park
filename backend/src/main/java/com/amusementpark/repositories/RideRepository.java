package com.amusementpark.repositories;

import com.amusementpark.models.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    Ride findByName(String name);
}