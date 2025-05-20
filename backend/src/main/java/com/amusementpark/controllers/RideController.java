package com.amusementpark.controllers;

import com.amusementpark.models.Ride;
import com.amusementpark.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    @Autowired
    private RideRepository rideRepository;

    @GetMapping("")
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    // Seed initial rides if not present
    @PostMapping("/seed")
    public ResponseEntity<?> seedRides() {
        if (rideRepository.count() == 0) {
            Ride r1 = new Ride();
            r1.setName("Roller Coaster");
            r1.setDescription("Exciting high-speed ride");
            r1.setPrice(20.0);
            Ride r2 = new Ride();
            r2.setName("Ferris Wheel");
            r2.setDescription("Enjoy the view from above");
            r2.setPrice(10.0);
            Ride r3 = new Ride();
            r3.setName("Haunted House");
            r3.setDescription("Spooky fun for all ages");
            r3.setPrice(15.0);
            rideRepository.saveAll(Arrays.asList(r1, r2, r3));
            return ResponseEntity.ok("Rides seeded");
        }
        return ResponseEntity.ok("Rides already exist");
    }
}