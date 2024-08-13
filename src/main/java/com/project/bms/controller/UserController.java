package com.project.bms.controller;

import com.project.bms.model.UserDTO;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.bms.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping({"", "/"})
    public String showUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/users/index";
    }

    @GetMapping("/create")
    public String showCreateUser(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO", userDTO);
        return "/users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        if (userDTO.getAvatar().isEmpty()) {
            result.addError(new FieldError("userDTO", "avatar", "Avatar is required"));
        }
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            result.addError(new FieldError("userDTO", "username", "Username already exists"));
        }
        if (result.hasErrors()) {
            return "/users/create";
        }

        // Save avatar
        MultipartFile avatar = userDTO.getAvatar();
        Date createAt = new Date();
        String filename = createAt.getTime() + "_" + avatar.getOriginalFilename();
        try {
            String uploadDir = "public/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = avatar.getInputStream()) {
                Path filePath = uploadPath.resolve(filename);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save avatar", e);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userService.hashPassword(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setFullname(userDTO.getFullname());
        user.setRole(userDTO.getRole());
        user.setAvatar(filename);
        userRepository.save(user);

        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditUser(Model model, @RequestParam Long id) {
        try {
            User user = userRepository.findById(id);
            model.addAttribute("user", user);
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFullname(user.getFullname());
            userDTO.setRole(user.getRole());
            model.addAttribute("userDTO", userDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/users";
        }
        return "/users/edit";
    }

    @PostMapping("/edit")
    public String editUser(Model model, @RequestParam Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        try {
            User user = userRepository.findById(id);
            model.addAttribute("user", user);

            if (result.hasErrors()) {
                return "/users/edit";
            }

            if (!userDTO.getAvatar().isEmpty()) {
                String uploadDir = "public/avatars/";
                Path oldAvatarPath = Paths.get(uploadDir + user.getAvatar());
                try {
                    Files.deleteIfExists(oldAvatarPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Save new avatar
                MultipartFile avatar = userDTO.getAvatar();
                Date createAt = new Date();
                String filename = createAt.getTime() + "_" + avatar.getOriginalFilename();

                try (InputStream inputStream = avatar.getInputStream()) {
                    Path filePath = Paths.get(uploadDir + filename);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                user.setAvatar(filename);
            }

            user.setUsername(userDTO.getUsername());
//            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());
            user.setFullname(userDTO.getFullname());
            user.setRole(userDTO.getRole());
            userRepository.update(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to edit user", e);
        }

        return "redirect:/users";
    }

    @GetMapping("/reset")
    public String reset(Model model, @RequestParam Long id) {
        try {
            User user = userRepository.findById(id);
            model.addAttribute("user", user);
            UserDTO userDTO = new UserDTO();
            model.addAttribute("userDTO", userDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/users";
        }
        return "/users/reset";
    }

    @PostMapping("/reset")
    public String resetPassword(Model model, @RequestParam Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                result.addError(new FieldError("user", "username", "User not found"));
            }
            if (result.hasErrors()) {
                return "/users/reset";
            }
            userService.reset(id, userDTO.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reset password", e);
        }
        return "redirect:/users";
    }
    @GetMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        try {
            User user = userRepository.findById(id);
            Path avatarPath = Paths.get("public/avatars/" + user.getAvatar());
            try {
                Files.deleteIfExists(avatarPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            userRepository.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/users";
    }
}