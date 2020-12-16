package com.example.bookfinder.util;

import com.example.bookfinder.controllers.HomeController;
import com.example.bookfinder.kafka.KafkaPriceConsumer;
import com.example.bookfinder.model.login.User;
import com.example.bookfinder.model.login.UserRepository;
import com.example.bookfinder.model.prices.PricesRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserUtil {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PricesRepository pricesRepository;

    private Logger logger = LoggerFactory.getLogger(UserUtil.class);
    private Map<String, KafkaPriceConsumer> consumers = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);
        return user;
    }

    public boolean checkIfBookExists(String id, User user) {
        if (user.getBooks() != null) {
            for (String singleBook : user.getBooks()) {
                if (singleBook.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void listenAsync(User user) throws IOException {
        Gson gson = new Gson();
        String username = user.getUsername();
        if(user.getBooks() != null && user.getBooks().size()>0){
            KafkaPriceConsumer consumer = new KafkaPriceConsumer(username,pricesRepository);
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
    }
}
