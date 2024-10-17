package com.droppart.userservice.controller;

import com.droppart.userservice.model.User;
import com.droppart.userservice.repository.UserRepository;
import com.droppart.userservice.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        userRepository.save(user);
        kafkaTemplate.send("user-registration-topic", "New User Registered: " + user.getUsername());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
