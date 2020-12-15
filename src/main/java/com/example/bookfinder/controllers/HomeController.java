package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.kafka.KafkaPriceConsumer;
import com.example.bookfinder.model.AjaxDTO;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.model.search.Book;
import com.example.bookfinder.model.search.SearchResult;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    private Map<String, KafkaPriceConsumer> consumers = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login/createuser")
    public ResponseEntity<AjaxDTO> createUser(@ModelAttribute User user) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        if (userRepository.findByUsername(user.getUsername()) == null) {
            try {
                User userToCreate = new User(user.getUsername(), encoder.encode(user.getPassword()));
                userRepository.save(userToCreate);
                ajaxDTO.setSuccess("User created you can now login");
                return new ResponseEntity<>(ajaxDTO, HttpStatus.OK);
            } catch (Exception e) {
                ajaxDTO.setError("Error when creating user");
                logger.error(e.getLocalizedMessage());
                return new ResponseEntity<>(ajaxDTO, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            String errorMessage = String.format("User with username: %s already exist", user.getUsername());
            ajaxDTO.setFailed(errorMessage);
            logger.info(errorMessage);
            return new ResponseEntity<>(ajaxDTO, HttpStatus.CONFLICT);
        }

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
            ajaxDTO.setSuccess(bookId);
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
                ajaxDTO.setSuccess(bookId);
                return new ResponseEntity<>(ajaxDTO, HttpStatus.OK);
            } catch (Exception ex) {
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

    @GetMapping("/listen")
    public String startKafkaListener() throws IOException {
        Gson gson = new Gson();
        User user = getCurrentUser();
        String username = user.getUsername();
        if(user.getBooks() != null && user.getBooks().size()>0){
            KafkaPriceConsumer consumer = new KafkaPriceConsumer(username);
            consumer.consume();
            consumers.put(username,consumer);
            logger.info("Started consumer for user: " + username);
            URI targetUrl = UriComponentsBuilder.fromUriString("http://localhost:9000")  // Build the base link
                    .path("/"+username)
                    .build()                                                 // Build the URL
                    .encode()                                                // Encode any URI items that need to be encoded
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(gson.toJson(user.getBooks()),headers);
            String result = restTemplate.postForObject(targetUrl,entity,String.class);
            System.out.println(result);
        }
        return "redirect:/";
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
