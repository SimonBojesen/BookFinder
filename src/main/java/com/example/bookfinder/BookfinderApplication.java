package com.example.bookfinder;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class BookfinderApplication {



    public static void main(String[] args) throws IOException {
        SpringApplication.run(BookfinderApplication.class, args);
       /* KafkaPriceConsumer consumer = new KafkaPriceConsumer("test");
        consumer.consume();*/


    }

}
