package com.amusementpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AmusementParkApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmusementParkApplication.class, args);
        
       // System.out.println("Amusement Park Application is running on port 8080...");

    }
 
}