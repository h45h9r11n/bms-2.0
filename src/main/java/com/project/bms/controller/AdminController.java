package com.project.bms.controller;

import com.project.bms.model.File;
import com.project.bms.model.User;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.AdminService;
import com.project.bms.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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
    public String admin(Model model, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                User user = userRepository.findById(sessionService.getUserId(request));
                model.addAttribute("user", user);
                return "admin";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/backup")
    public String showBackup(Model model, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                User user = userRepository.findById(sessionService.getUserId(request));
                model.addAttribute("user", user);
                File file = new File();
                model.addAttribute("file", file);
                return "backup";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/backup")
    public String backup(Model model, HttpServletRequest request, @Valid @ModelAttribute File file, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                User user = userRepository.findById(sessionService.getUserId(request));
                model.addAttribute("user", user);

                if (file.getFilename().isEmpty()) {
                    result.addError(new FieldError("file", "filename", "Filename is required"));
                }

                String backupFileName = adminService.backup(file.getFilename());

                if (backupFileName != null) {
                    result.addError(new FieldError("file", "filename", "Backup successful"));
                    model.addAttribute("backupFileName", backupFileName);
                } else {
                    model.addAttribute("backupFileName", null);
                    result.addError(new FieldError("file", "filename", "Backup failed"));
                }

                if (result.hasErrors()) {
                    return "backup";
                }
            }
        }
        return "redirect:/backup";
    }
}
