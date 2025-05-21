package com.amusementpark.repositories;

import com.amusementpark.models.Ride;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    Ride findByName(String name);
    List<Ride> findByCategory(String category);
}