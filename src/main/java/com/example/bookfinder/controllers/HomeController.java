package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.AjaxDTO;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.model.search.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        model.addAttribute("exists", checkIfBookExists(id, getCurrentUser()));
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
    public ResponseEntity<AjaxDTO> subscribeToBookAuthors(@RequestParam String bookId) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        User user = getCurrentUser();
        if (checkIfBookExists(bookId, user)) {
            String errorMessage = String.format("You are already subscribed to this book with id: %s", bookId);
            ajaxDTO.setFailed(errorMessage);
            logger.warn(errorMessage);
            return new ResponseEntity<>(ajaxDTO, HttpStatus.CONFLICT);
        }
        try {
            if (user.getBooks() == null) {
                user.setBooks(new ArrayList<>());
            }
            user.getBooks().add(bookId);
            userRepository.save(user);
            ajaxDTO.setSucces(bookId);
            return new ResponseEntity<>(ajaxDTO, HttpStatus.OK);
        } catch (Exception ex) {
            ajaxDTO.setError("Internal server error: Something went wrong");
            logger.error(ex.getLocalizedMessage());
            return new ResponseEntity<>(ajaxDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/book/subscribe")
    public ResponseEntity<AjaxDTO> unSubscribeBook(@RequestParam String bookId) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        User user = getCurrentUser();
        if (checkIfBookExists(bookId, user)) {
            try {
                int i = user.getBooks().indexOf(bookId);
                user.getBooks().remove(i);
                userRepository.save(user);
                ajaxDTO.setSucces(bookId);
                return new ResponseEntity<>(ajaxDTO, HttpStatus.OK);
            }catch (Exception ex) {
                ajaxDTO.setError("Internal server error: Something went wrong");
                logger.error(ex.getLocalizedMessage());
                return new ResponseEntity<>(ajaxDTO, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            String errorMessage = String.format("You are not subscribed to this book with id: %s", bookId);
            ajaxDTO.setFailed(errorMessage);
            logger.info(errorMessage);
            return new ResponseEntity<>(ajaxDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        return "root.html";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "login.html";
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);
        return user;
    }

    private boolean checkIfBookExists(String id, User user) {
        if (user.getBooks() != null) {
            for (String singleBook : user.getBooks()) {
                if (singleBook.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
