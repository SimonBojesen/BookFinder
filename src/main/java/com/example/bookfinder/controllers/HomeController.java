package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.search.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
public class homeController {
    @GetMapping("/")
    public String getHomePage(Model model) {
        return "root.html";
    }
}
