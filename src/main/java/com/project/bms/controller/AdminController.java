package com.project.bms.controller;

import com.project.bms.repository.UserRepository;
import com.project.bms.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin")
    public String admin(HttpServletRequest request) {
        if (sessionService.getCookies(request) == null) {
            return "redirect:/";
        } else {
            String sessionId = sessionService.getSessionId(sessionService.getCookies(request));
            Long userid = sessionService.getUserId(sessionId);
            String role = userRepository.findById(userid).getRole();
            if (role.equals("ADMIN")) {
                return "admin";
            }
        }
        return "redirect:/";
    }
}
