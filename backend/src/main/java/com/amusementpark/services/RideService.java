package com.amusementpark.services;

import com.amusementpark.models.Ride;
import com.amusementpark.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class RideService {
    
    @Autowired
    private RideRepository rideRepository;
    
    /**
     * Get all rides
     * @return List of all rides
     */
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }
    
    /**
     * Get rides by category
     * @param category The ride category
     * @return List of rides in the category
     */
    public List<Ride> getRidesByCategory(String category) {
        return rideRepository.findByCategory(category);
    }
    
    /**
     * Initialize rides if none exist
     */
    @PostConstruct
    public void initRides() {
        if (rideRepository.count() == 0) {
            // Create Thrill Rides
            rideRepository.save(new Ride("The Thunderbolt", "A high-speed roller coaster with loops, steep drops, and adrenaline-pumping turns.", "THRILL"));
            rideRepository.save(new Ride("Sky Drop", "Experience a sudden vertical drop from great height — not for the faint of heart!", "THRILL"));
            rideRepository.save(new Ride("Vortex Spinner", "A spinning coaster that twists you through a vortex of fun and fear.", "THRILL"));
            rideRepository.save(new Ride("Storm Surge", "A rapid water rafting adventure through wild artificial rapids.", "THRILL"));
            rideRepository.save(new Ride("Fire Loop", "A looping inverted coaster with flame-themed decor and wild inversions.", "THRILL"));
            
            // Create Family-Friendly Rides
            rideRepository.save(new Ride("Fantasy Carousel", "A magical merry-go-round ride with beautifully designed horses and music.", "FAMILY"));
            rideRepository.save(new Ride("Jungle Safari Ride", "Ride through an artificial jungle filled with lifelike animal animatronics.", "FAMILY"));
            rideRepository.save(new Ride("Adventure Boat", "A relaxing water ride that sails through miniature islands and waterfalls.", "FAMILY"));
            rideRepository.save(new Ride("Haunted Mansion", "Explore spooky corridors and ghostly effects in this family-safe scary house.", "FAMILY"));
            rideRepository.save(new Ride("Bumper Cars", "Classic fun for everyone — bump into your friends and family!", "FAMILY"));
            
            // Create Kids Rides
            rideRepository.save(new Ride("Mini Ferris Wheel", "A kid-sized ferris wheel with a gentle height and colorful lights.", "KIDS"));
            rideRepository.save(new Ride("Buggy Track", "Mini cars on a closed track that kids can drive around freely.", "KIDS"));
            rideRepository.save(new Ride("Tiny Flyers", "Small flying swings perfect for young adventurers who want to soar.", "KIDS"));
            rideRepository.save(new Ride("Magic Train", "A mini train that takes kids through magical tunnels and fairy tale lands.", "KIDS"));
            rideRepository.save(new Ride("Ball Pit Zone", "A huge area filled with soft colorful balls and slides.", "KIDS"));
            
            // Create Themed Experiences
            rideRepository.save(new Ride("Park Express Monorail", "Ride above the park with a full scenic view of attractions and zones.", "THEMED"));
            rideRepository.save(new Ride("Sky Gliders", "Soar slowly over the park like a bird with suspended chair lifts.", "THEMED"));
            rideRepository.save(new Ride("Lazy River", "Float along a peaceful water stream in an inflatable tube.", "THEMED"));
            rideRepository.save(new Ride("4D Adventure Theater", "Enjoy immersive animated stories with motion chairs and real effects.", "THEMED"));
            rideRepository.save(new Ride("Glow Tunnel Walk", "A walk-through tunnel with glowing lights, mirrors, and illusions.", "THEMED"));
        }
    }
}