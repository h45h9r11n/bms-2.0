package com.project.bms.controller;

import com.project.bms.model.Session;
import com.project.bms.model.User;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.SessionService;
import com.project.bms.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@Controller

public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

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
        if (userService.register(user.getUsername(), user.getPassword(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Redirecting to login...");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
        }
    }

    @PostMapping("/req/login")
    @ResponseBody
    public ResponseEntity login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        if (userService.login(user.getUsername(), user.getPassword())) {
            Session session = sessionService.createSession(userRepository.findByUsername(user.getUsername()).getId());
            Cookie sessionCookie = new Cookie("SESSIONID", session.getSessionId());
            sessionCookie.setMaxAge(60 * 60);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            response.addCookie(sessionCookie);
            return ResponseEntity.status(HttpStatus.OK).body("Login successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password!");
        }
    }
}
