package com.project.bms.service;

import com.project.bms.model.Session;
import com.project.bms.model.User;
import com.project.bms.repository.UserRepository;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean register(String username, String password, String email) {
        if (userRepository.findByUsername(username) != null) {
            return false; // Username already exists
        }

        String hashedPassword = hashPassword(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setEmail(email);
        userRepository.save(newUser);
        return true;
    }

    public boolean login(String username, String password) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
//            System.out.println(hashPassword(password));
            return false; // User not found
        }

        if (hashPassword(password).equals(user.getPassword())) {
            return true; // Login successful
        } else {
            return false; // Incorrect password
        }


    }

    public boolean reset(Long id, String password){
        User user = userRepository.findById(id);
        if (user == null) {
            return false; // User not found
        }
        System.out.println(hashPassword(password));
        userRepository.updatePassword(id, hashPassword(password));
        return true;
    }

//    public User getCurrentUser() {
//    }


}
