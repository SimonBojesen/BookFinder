package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.login.BookDTO;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.model.search.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login/createuser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userToCreate = new User(user.getUsername(), encoder.encode(user.getPassword()));
        userRepository.save(userToCreate);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/bookdetails/{id}")
    public String getBookDetails(@PathVariable String id, Model model) {
        URI targetUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com")  // Build the base link
                .path("/books/v1/volumes/" + id)                            // Add path
                .queryParam("key", Configuration.googleApiKey)
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();
        Book result = restTemplate.getForObject(targetUrl, Book.class);
        model.addAttribute("book", result);
        return "bookdetails.html";
    }

    @PostMapping("/book/subscribe")
    public ResponseEntity<User> subscribeToBookAuthors(@ModelAttribute BookDTO book){
        try {
            //String username = ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username= ((UserDetails)principal).getUsername();
            User user = userRepository.findByUsername(username);
            if (user.getBooks() == null)
                user.setBooks(new ArrayList<>());
            user.getBooks().add(book);
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        return "root.html";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "login.html";
    }
}
