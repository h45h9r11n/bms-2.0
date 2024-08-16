package com.project.bms.controller;
import com.project.bms.model.Session;
import com.project.bms.model.User;
import com.project.bms.repository.SessionRepository;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.SessionService;
import com.project.bms.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller

public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (sessionService.isLogged(request)){
            Cookie[] cookies = sessionService.getCookies(request);
            Session session = sessionRepository.findBySessionId(sessionService.getSessionId(cookies));
            if (session != null){
                if (!sessionService.isExpired(session)) {
                    return "redirect:/users/home";
                }
            }
        }
        return "login";
    }

    @GetMapping("/signup")
    public String register() {
        return "signup";
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity register(@RequestBody User user) {
        if (userService.register(user.getUsername(), user.getPassword(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Redirecting to login...");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody User user, HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();
        if (userService.login(user.getUsername(), user.getPassword())) {
            Session session = sessionService.createSession(userRepository.findByUsername(user.getUsername()).getId());
            Cookie sessionCookie = new Cookie("SESSIONID", session.getSessionId());
            sessionCookie.setMaxAge(60 * 60);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            response.addCookie(sessionCookie);

            String role = userRepository.findByUsername(user.getUsername()).getRole();
            Cookie roleCookie = new Cookie("ROLE", role);
            roleCookie.setMaxAge(60 * 60);
            roleCookie.setPath("/");
            roleCookie.setHttpOnly(false);
            response.addCookie(roleCookie);

            String redirectUrl = "/login"; //
            if ("ADMIN".equalsIgnoreCase(role)) {
                redirectUrl = "/admin";
            } else {
                redirectUrl = "/users/home";
            }
            responseBody.put("redirectUrl", redirectUrl);
            return ResponseEntity.ok(responseBody);
        }

        responseBody.put("redirectUrl", "/login");
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        if (!sessionService.isLogged(request)){
            return "redirect:/login";
        }
        String sessionId = sessionService.getSessionId(sessionService.getCookies(request));
        sessionService.deleteSession(sessionId);
        Cookie sessionCookie = new Cookie("SESSIONID", "");
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        response.addCookie(sessionCookie);

        Cookie roleCookie = new Cookie("ROLE", "");
        roleCookie.setMaxAge(0);
        roleCookie.setPath("/");
        roleCookie.setHttpOnly(true);
        response.addCookie(roleCookie);

        return "redirect:/login";
    }
}


