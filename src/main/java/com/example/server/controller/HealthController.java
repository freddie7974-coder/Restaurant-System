package com.example.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status",    "UP",
            "service",   "Restaurant Server v2.0",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
