package com.project.bms.controller;

import com.project.bms.model.User;
import com.project.bms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller

public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/req/login")
    public String login() {
        return "login";
    }

    @GetMapping("/req/signup")
    public String register() {
        return "signup";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/req/signup")
    @ResponseBody
    public ResponseEntity register(@RequestBody User user) {
//        if (!user.password.equals(confirmPassword)) {
//            return "Passwords do not match!";
//        }
        if (userService.register(user.getUsername(), user.getPassword(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Redirecting to login...");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
        }
    }

    @PostMapping("/req/login")
    @ResponseBody
    public ResponseEntity login(@RequestBody User user) {
        if (userService.login(user.getUsername(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body("Login successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password!");
        }
    }






}
