package com.example.bookfinder.controllers;

import com.example.bookfinder.model.AjaxDTO;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class HomeController {

    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private PasswordEncoder encoder;


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

    @GetMapping("/")
    public String getHomePage(Model model) {
        return "root";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/listen")
    public String startKafkaListener() throws IOException {
        User user = userUtil.getCurrentUser();
        //  async
        userUtil.listenToKafka(user);

        return "redirect:/booklist";
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
