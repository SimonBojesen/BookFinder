package com.example.bookfinder.controllers;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.AjaxDTO;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.model.prices.Prices;
import com.example.bookfinder.model.prices.PricesRepository;
import com.example.bookfinder.model.search.Book;
import com.example.bookfinder.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SubscribeController {

    @Autowired
    private UserUtil userUtil;
    @Autowired
    private PricesRepository pricesRepository;
    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @PostMapping("/book/subscribe")
    public ResponseEntity<AjaxDTO> subscribeToBookAuthors(@RequestParam String bookId) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        User user = userUtil.getCurrentUser();
        if (userUtil.checkIfBookExists(bookId, user)) {
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

    @GetMapping("/booklist")
    public String getBookListPage(Model model)
    {
        User user = userUtil.getCurrentUser();
        List<String> bookIdList = user.getBooks();
        List<Prices> priceList = new ArrayList<>();
        for (String bookId: bookIdList) {
            Prices price = pricesRepository.findByBookId(bookId);
            if (price != null) {
                priceList.add(pricesRepository.findByBookId(bookId));
            }
            else {
                URI targetUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com")  // Build the base link
                        .path("/books/v1/volumes/" + bookId)                            // Add path
                        .queryParam("key", Configuration.googleApiKey)
                        .build()                                                 // Build the URL
                        .encode()                                                // Encode any URI items that need to be encoded
                        .toUri();
                Book result = restTemplate.getForObject(targetUrl, Book.class);
                priceList.add(new Prices(bookId, result.getVolumeInfo().getTitle(), null));
            }
        }
        model.addAttribute("priceList", priceList);
        return "subscribedbooks";
    }


    @DeleteMapping("/book/subscribe")
    public ResponseEntity<AjaxDTO> unSubscribeBook(@RequestParam String bookId) {
        AjaxDTO ajaxDTO = new AjaxDTO();
        User user = userUtil.getCurrentUser();
        if (userUtil.checkIfBookExists(bookId, user)) {
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


}
