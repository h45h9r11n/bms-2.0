package com.project.bms.controller;

import com.project.bms.repository.UserRepository;
import com.project.bms.service.AdminService;
import com.project.bms.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@Controller
public class AdminController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin")
    public String admin(HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                return "admin";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/backup")
    public String showBackup(Model model, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                File file = new File("");
                model.addAttribute("File", file);
                return "backup";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/backup")
    public String backup(HttpServletRequest request, @Valid @ModelAttribute com.project.bms.model.File file, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                if (file.getFilename().isEmpty()) {
                    result.addError(new FieldError("file", "filename", "filename is required"));
                }
                if (result.hasErrors()) {
                    return "backup";
                }

                System.out.println(file.getFilename());
                if (adminService.backup(file.getFilename())) {
                    return "redirect:/admin";
                }
            }
        }
        return "redirect:/backup";
    }
}
