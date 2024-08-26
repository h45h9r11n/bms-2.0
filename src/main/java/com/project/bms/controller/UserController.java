package com.project.bms.controller;

import com.project.bms.model.Book;
import com.project.bms.model.Query;
import com.project.bms.model.UserDTO;
import com.project.bms.repository.BookRepository;
import com.project.bms.repository.SessionRepository;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.SessionService;
import com.project.bms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.bms.model.User;
import org.springframework.web.filter.RequestContextFilter;
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
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private RequestContextFilter requestContextFilter;

    @GetMapping({"", "/"})
    public String showUsers(Model model, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                User user = userRepository.findById(sessionService.getUserId(request));
                model.addAttribute("user", user);
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
                return "/users/index";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/create")
    public String showCreateUser(HttpServletRequest request, Model model) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                User user = userRepository.findById(sessionService.getUserId(request));
                model.addAttribute("user", user);
                UserDTO userDTO = new UserDTO();
                model.addAttribute("userDTO", userDTO);
                return "/users/create";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/create")
    public String createUser(HttpServletRequest request, @Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
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
        }
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String showEditUser(HttpServletRequest request, Model model, @RequestParam Long id) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request) || (sessionService.getUserId(request) == id)) {
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
                }
                return "/users/edit";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String editUser(HttpServletRequest request, Model model, @RequestParam Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request) || (sessionService.getUserId(request) == id)) {
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


                    user.setEmail(userDTO.getEmail());
                    user.setFullname(userDTO.getFullname());
                    if (sessionService.getCookies(request) != null) {
                        if (sessionService.isAdmin(request)) {
                            user.setRole(userDTO.getRole());
                        }
                    }
                    userRepository.update(user);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to edit user", e);
                }
                if (sessionService.isAdmin(request)){
                    return "redirect:/users";
                } else {
                    return "redirect:/users/view?id=" + id;
                }

            }
        }
        return "redirect:/";
    }

    @GetMapping("/reset")
    public String reset(HttpServletRequest request, Model model, @RequestParam Long id) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request) || (sessionService.getUserId(request) == id)) {
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
        }
        return "redirect:/";
    }

    @PostMapping("/reset")
    public String resetPassword(HttpServletRequest request, Model model, @RequestParam Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request) || (sessionService.getUserId(request) == id)) {
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
                if (sessionService.isAdmin(request)){
                    return "redirect:/users";
                } else {
                    return "redirect:/users/view";
                }

            }

        }
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteUser(HttpServletRequest request, @RequestParam Long id) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
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
        return "redirect:/";
    }

    @GetMapping("/view")
    public String showUser(Model model, @RequestParam Long id, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request) || (sessionService.getUserId(request) == id)) {
                try {
                    User user = userRepository.findById(id);
                    model.addAttribute("user", user);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/users";
                }
                return "/users/profile";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/home")
    public String showHome(Model model, HttpServletRequest request) {
        if (!sessionService.isSessionExpired(request)){
            User user = userRepository.findById(sessionService.getUserId(request));
            model.addAttribute("user", user);
            Query query = new Query();
            model.addAttribute("query", query);
            List<Book> books = bookRepository.findAll();
            model.addAttribute("books", books);
            return "/users/home";
        }
        return "redirect:/";
    }





}