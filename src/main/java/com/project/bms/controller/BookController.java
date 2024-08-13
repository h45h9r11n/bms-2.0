package com.project.bms.controller;

import com.project.bms.model.BookDTO;
import com.project.bms.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.bms.model.Book;
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
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping({"", "/"})
    public String showBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "/books/index";
    }

    @GetMapping("/create")
    public String showCreateBook(Model model) {
        BookDTO bookDTO = new BookDTO();
        model.addAttribute("bookDTO", bookDTO);
        return "/books/create";
    }

    @PostMapping("/create")
    public String createBook(@Valid @ModelAttribute BookDTO bookDTO, BindingResult result) throws IOException {
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


    @GetMapping("/edit")
    public String showEditBook(Model model, @RequestParam Long id) {
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

    @PostMapping("/edit")
    public String editBook(Model model, @RequestParam Long id, @Valid @ModelAttribute BookDTO bookDTO, BindingResult result) throws IOException {
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

    @GetMapping("/delete")
    public String deleteBook(@RequestParam Long id) {
        try {
            Book book = bookRepository.findById(id);
            Path imagePath = Paths.get("public/images" + book.getImage());
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