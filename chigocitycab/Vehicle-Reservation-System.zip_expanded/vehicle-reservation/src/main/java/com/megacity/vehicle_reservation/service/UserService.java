package com.megacity.vehicle_reservation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.megacity.vehicle_reservation.entity.User;
import com.megacity.vehicle_reservation.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // Constructor to inject the password encoder
    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Register a new user with a hashed password
    public User registerUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hashing the password
        user.setRole(role);
        return userRepository.save(user);
    }

    // Validate login credentials by comparing hashed passwords
    public boolean validateLogin(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword()); // Checking hashed password
    }

    // Get user role
    public String getUserRole(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getRole).orElse(null);
    }
}
