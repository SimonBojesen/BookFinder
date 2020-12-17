package com.example.bookfinder;

import com.example.bookfinder.kafka.KafkaPriceConsumer;
import com.example.bookfinder.model.login.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

import java.io.IOException;

@SpringBootApplication
public class BookfinderApplication {



    public static void main(String[] args) throws IOException {
        SpringApplication.run(BookfinderApplication.class, args);
       /* KafkaPriceConsumer consumer = new KafkaPriceConsumer("test");
        consumer.consume();*/


    }

}
