package com.example.bookfinder.controllers;


import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.AjaxDTO;
import com.example.bookfinder.model.review.AverageModel;
import com.example.bookfinder.model.review.GoogleReviewModel;
import com.example.bookfinder.model.review.ReviewModel;
import com.example.bookfinder.model.review.TotalReviewsModel;
import com.example.bookfinder.model.search.Book;
import com.example.bookfinder.model.search.IndustryIdentifiers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/review")
public class ReviewController {
    private Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @PostMapping("")
    public ResponseEntity<AjaxDTO> postReview(@ModelAttribute ReviewModel model) {
        AjaxDTO dto = new AjaxDTO();
        if (model != null) {
            try {
                String json = gson.toJson(model);
                URI targetUrl = UriComponentsBuilder.fromUriString(Configuration.bookReviewApi)  // Build the base link
                        .path("/review/")                            // Add path
                        .build()                                                 // Build the URL
                        .encode()                                                // Encode any URI items that need to be encoded
                        .toUri();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);
                String result = restTemplate.postForObject(targetUrl, entity, String.class);
                dto.setSuccess("Review successfully created: " + result);
                logger.info("Created review: " + result);
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } catch (Exception ex) {
                logger.error("Exception in api call", ex);
                dto.setError("Something went wrong");
                return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            logger.warn("Model was null, invalid input");
            dto.setFailed("Invalid input");
            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<AjaxDTO> getReview(@PathVariable String bookId) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        GoogleReviewModel googleReviews = getGoogleRatings(bookId);
        try {
            if (googleReviews == null) {
                logger.warn("No ISBN13 found for bookId: " + bookId);
                ajaxDTO.setFailed("No review available. Reviews are only available for books with ISBN-13");
                return new ResponseEntity<>(ajaxDTO, HttpStatus.NOT_ACCEPTABLE);
            } else {
                URI targetUrl = UriComponentsBuilder.fromUriString(Configuration.bookReviewApi)  // Build the base link
                        .path("/review/average/" + googleReviews.getIsbn13())                            // Add path
                        .build()                                                 // Build the URL
                        .encode()                                                // Encode any URI items that need to be encoded
                        .toUri();
                AverageModel result = restTemplate.getForObject(targetUrl, AverageModel.class);
                TotalReviewsModel calculatedAverage = calculateAverage(result.getReviewAmount(), googleReviews.getAmount(), result.getAverageRating(), googleReviews.getRating());
                ajaxDTO.setModel(calculatedAverage);
                ajaxDTO.setSuccess("Calculated average successfully");
                logger.info("Calculated average successfully for bookId " + bookId);
                return new ResponseEntity<>(ajaxDTO, HttpStatus.OK);
            }
        } catch (Exception ex) {
            logger.error("Exception in api call", ex);
            ajaxDTO.setError("Something went wrong");
            return new ResponseEntity<>(ajaxDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private GoogleReviewModel getGoogleRatings(String bookId) {
        URI targetUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com")  // Build the base link
                .path("/books/v1/volumes/" + bookId)                            // Add path
                .queryParam("key", Configuration.googleApiKey)
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();
        Book result = restTemplate.getForObject(targetUrl, Book.class);
        String isbn13 = null;
        if(result.getVolumeInfo().getIndustryIdentifiers() != null) {
            for (IndustryIdentifiers industryIdentifier : result.getVolumeInfo().getIndustryIdentifiers()) {
                if (industryIdentifier.getType().equals("ISBN_13")) {
                    isbn13 = industryIdentifier.getIdentifier();
                    break;
                }
            }
        }
        if (isbn13 == null) {
            return null;
        } else {
            return new GoogleReviewModel(result.getVolumeInfo().getRatingsCount(), result.getVolumeInfo().getAverageRating(), isbn13, bookId);
        }
    }

    private TotalReviewsModel calculateAverage(Integer reviewAmount, Integer googleReviewAmount, Double average, Double googleAverage) {
        if(reviewAmount!= null && googleReviewAmount != null && average != null && googleAverage != null){
            double total = reviewAmount * average;
            double googleTotal = googleReviewAmount * googleAverage;
            double result = (total + googleTotal) / (reviewAmount + googleReviewAmount);
            int reviews = reviewAmount + googleReviewAmount;
            result = Math.round(result * 100.0) / 100.0;
            return new TotalReviewsModel(reviews,result);

        }else if(reviewAmount != null && average != null){
            average = Math.round(average * 100.0) / 100.0;
            return new TotalReviewsModel(reviewAmount,average);
        }else if(googleReviewAmount!= null && googleAverage != null){
            googleAverage = Math.round(googleAverage * 100.0) / 100.0;
            return new TotalReviewsModel(googleReviewAmount,googleAverage);
        }
        return null;

    }
}
