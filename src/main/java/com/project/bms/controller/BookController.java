package com.project.bms.controller;

import com.project.bms.model.BookDTO;
import com.project.bms.model.Comment;
import com.project.bms.model.CommentDTO;
import com.project.bms.repository.BookRepository;
import com.project.bms.repository.CommentRepository;
import com.project.bms.repository.SessionRepository;
import com.project.bms.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.bms.model.Book;
import org.springframework.web.multipart.MultipartFile;
import com.project.bms.model.Query;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    @GetMapping({"", "/"})
    public String showBooks(HttpServletRequest request, Model model) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                List<Book> books = bookRepository.findAll();
                model.addAttribute("books", books);
                return "/books/index";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/view")
    public String showBook(HttpServletRequest request, Model model, @RequestParam Long id) {
        if (sessionService.isSessionExpired(request)){
            return "redirect:/";
        }
        try{
            //get book's information
            Book book = bookRepository.findById(id);
            model.addAttribute("book", book);
            //get book's comments
            List<Comment> comments = commentRepository.findByBookId(id);
            CommentDTO commentDTO = new CommentDTO();
            model.addAttribute("comments", comments);
            model.addAttribute("commentDTO", commentDTO);
        } catch (Exception e){
            e.printStackTrace();
            return "redirect:/books";
        }
        return "/books/profile";
    }

    @PostMapping("/comment")
    public String createComment(HttpServletRequest request, @Valid @ModelAttribute CommentDTO commentDTO, BindingResult result) {
        if (sessionService.isSessionExpired(request)){
            return "redirect:/";
        }
        if (commentDTO.getContent().isEmpty()){
            result.addError(new FieldError("commentDTO", "content", "Content is required"));
        }
        if (result.hasErrors()){
            return "redirect:/books/view?id=" + commentDTO.getBookid();
        }

        Cookie[] cookies = request.getCookies();
        Long userId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("SESSIONID")) {
                    String sessionId = cookie.getValue();
                    userId = sessionRepository.findBySessionId(sessionId).getUserId();
                }
            }
        }

        Comment comment = new Comment();
        comment.setBookid(Long.valueOf(commentDTO.getBookid()));
        comment.setUserid(userId);
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);

        return "redirect:/books/view?id=" + comment.getBookid();
    }

    @GetMapping("/create")
    public String showCreateBook(Model model, HttpServletRequest request) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                BookDTO bookDTO = new BookDTO();
                model.addAttribute("bookDTO", bookDTO);
                return "/books/create";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/create")
    public String createBook(HttpServletRequest request, @Valid @ModelAttribute BookDTO bookDTO, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                if (bookDTO.getImage().isEmpty()) {
                    result.addError(new FieldError("bookDTO", "image", "Image is required"));
                }
                if (result.hasErrors()) {
                    return "/books/create";
                }

                //save image
                MultipartFile image = bookDTO.getImage();
                Date createAt = new Date();
                String filename = createAt.getTime() + "_" + image.getOriginalFilename();
                try {
                    String uploadDir = "public/images/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        try {
                            Files.createDirectories(uploadPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try (InputStream inputStream = image.getInputStream()) {
                        Path filePath = uploadPath.resolve(filename);
                        Files.copy(inputStream, Paths.get(uploadDir + filename));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }

                Book book = new Book();
                book.setTitle(bookDTO.getTitle());
                book.setAuthor(bookDTO.getAuthor());
                book.setDescription(bookDTO.getDescription());
                book.setPrice(bookDTO.getPrice());
                book.setImage(filename);
                bookRepository.save(book);
                return "redirect:/books";
            }
        }
        return "redirect:/";
    }


    @GetMapping("/edit")
    public String showEditBook(HttpServletRequest request, Model model, @RequestParam Long id) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                try {
                    Book book = bookRepository.findById(id);
                    model.addAttribute("book", book);
                    BookDTO bookDTO = new BookDTO();
                    bookDTO.setTitle(book.getTitle());
                    bookDTO.setAuthor(book.getAuthor());
                    bookDTO.setDescription(book.getDescription());
                    bookDTO.setPrice(book.getPrice());
                    model.addAttribute("bookDTO", bookDTO);

                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/books";
                }
                return "/books/edit";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String editBook(HttpServletRequest request, Model model, @RequestParam Long id, @Valid @ModelAttribute BookDTO bookDTO, BindingResult result) throws IOException {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                try {
                    Book book = bookRepository.findById(id);
                    model.addAttribute("book", book);

                    if (result.hasErrors()) {
                        return "/books/edit";
                    }

                    if (!bookDTO.getImage().isEmpty()) {
                        String uploadDir = "public/images/";
                        Path oldImagePath = Paths.get(uploadDir + bookDTO.getImage());
                        try {
                            Files.delete(oldImagePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //save new image file
                        MultipartFile image = bookDTO.getImage();
                        Date createAt = new Date();
                        String filename = createAt.getTime() + "_" + image.getOriginalFilename();

                        try (InputStream inputStream = image.getInputStream()) {
                            Files.copy(inputStream, Paths.get(uploadDir + filename), StandardCopyOption.REPLACE_EXISTING);
                        }

                        book.setImage(filename);

                    }
                    if (!bookDTO.getTitle().equals(book.getTitle())){
                        book.setTitle(bookDTO.getTitle());
                    }

                    if (!bookDTO.getAuthor().equals(book.getAuthor())){
                        book.setAuthor(bookDTO.getAuthor());
                    }

                    if (!bookDTO.getDescription().equals(book.getDescription())){
                        book.setDescription(bookDTO.getDescription());
                    }

                    if (bookDTO.getPrice() != book.getPrice()){
                        book.setPrice(bookDTO.getPrice());
                    }

                    bookRepository.save(book);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return "redirect:/books";
            }
        }

        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteBook(HttpServletRequest request, @RequestParam Long id) {
        if (sessionService.getCookies(request) != null) {
            if (sessionService.isAdmin(request)) {
                try {
                    Book book = bookRepository.findById(id);
                    Path imagePath = Paths.get("public/images/" + book.getImage());
                    try {
                        Files.delete(imagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bookRepository.delete(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "redirect:/books";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/search")
    public String searchBook(HttpServletRequest request, Model model, @Valid @ModelAttribute Query query, BindingResult result) throws IOException {
        if (sessionService.isSessionExpired(request)) {
            return "redirect:/";
        }
        try {
            String title = query.getContent();
            if (title == null) {
                result.addError(new FieldError("query", "content", "Content is required"));
            }
            if (result.hasErrors()) {
                return "redirect:/users/home";
            }

            List<Book> books = bookRepository.findByTitleContaining(title);
            model.addAttribute("books", books);
        } catch (Exception e) {
                throw new RuntimeException(e);
        }

        return "/books/searchresult";
    }

}