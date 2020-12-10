package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.search.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
public class SearchController {

    Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/search/{query}")
    public SearchResult getBooks(@PathVariable("query") String query) {
        URI targetUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com")  // Build the base link
                .path("/books/v1/volumes")                            // Add path
                .queryParam("key", Configuration.googleApiKey).queryParam("q", query)                                // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();
        return restTemplate.getForObject(targetUrl, SearchResult.class);
    }


    @GetMapping("/booklist/{query}")
    public String showBooks(@PathVariable("query") String query, Model model) {
      populateModel(query, 0, null, model);
        return "booklist.html";
    }

    @GetMapping("/booklist/{query}/{startIndex}/{maxResults}")
    public String showBooks(@PathVariable("query") String query, @PathVariable("startIndex") int startIndex, @PathVariable("maxResults") Integer maxResults, Model model) {
        populateModel(query, startIndex, maxResults, model);
        return "booklist.html";
    }

    @GetMapping("/booklist/{query}/{startIndex}")
    public String showBooks(@PathVariable("query") String query, @PathVariable("startIndex") int startIndex, Model model) {
        populateModel(query, startIndex, null, model);
        return "booklist.html";
    }

    private void populateModel(String query, int startIndex, Integer maxResults, Model model) {
        String errorMessage;
        if (maxResults < 0 || maxResults > 40) {
            errorMessage = String.format("MaxResults must be a number between 0 and 40. Value was  %s", maxResults);
            model.addAttribute("resultError", errorMessage);
            logger.warn(errorMessage);
            return;
        }
        else if(startIndex < 0) {
            errorMessage = String.format("StartIndex must be 0 or above. Value was  %s", startIndex);
            model.addAttribute("resultError", errorMessage);
            logger.warn(errorMessage);
            return;
        }
        else if (maxResults == null ) {
            maxResults = 10;
        }

        URI targetUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com")  // Build the base link
                .path("/books/v1/volumes")                            // Add path
                .queryParam("key", Configuration.googleApiKey)
                .queryParam("q", query)
                .queryParam("maxResults", maxResults)
                .queryParam("startIndex", startIndex)// Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();
        SearchResult result = restTemplate.getForObject(targetUrl, SearchResult.class);
        model.addAttribute("searchResult", result);

    }


}
