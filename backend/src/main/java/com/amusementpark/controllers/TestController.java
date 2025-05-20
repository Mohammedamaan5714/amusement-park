package com.amusementpark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple test controller to verify frontend-backend integration
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * Simple ping endpoint to test connectivity
     * @return a simple response message
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Backend is connected!");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Echo endpoint that returns the message sent from frontend
     * @param requestBody Map containing the message to echo
     * @return the same message with a prefix
     */
    @PostMapping("/echo")
    public ResponseEntity<Map<String, String>> echo(@RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Echo from backend: " + message);
        return ResponseEntity.ok(response);
    }
}