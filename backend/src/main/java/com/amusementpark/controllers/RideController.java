package com.amusementpark.controllers;

import com.amusementpark.models.Ride;
import com.amusementpark.repositories.RideRepository;
import com.amusementpark.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    @Autowired
    private RideRepository rideRepository;
    
    @Autowired
    private RideService rideService;

    @GetMapping("")
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }
    
    /**
     * Get rides by category
     * @param category The ride category
     * @return List of rides in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Ride>> getRidesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(rideService.getRidesByCategory(category));
    }
    
    /**
     * Get rides grouped by category
     * @return Map of category to list of rides
     */
    @GetMapping("/grouped")
    public ResponseEntity<Map<String, List<Ride>>> getRidesGroupedByCategory() {
        List<Ride> allRides = rideService.getAllRides();
        
        Map<String, List<Ride>> groupedRides = allRides.stream()
                .collect(Collectors.groupingBy(Ride::getCategory));
        
        return ResponseEntity.ok(groupedRides);
    }
}