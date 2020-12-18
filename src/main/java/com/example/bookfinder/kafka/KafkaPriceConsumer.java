package com.example.bookfinder.kafka;

import com.example.bookfinder.config.Configuration;
import com.example.bookfinder.model.prices.Prices;
import com.example.bookfinder.model.prices.PricesRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

public class KafkaPriceConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaPriceConsumer.class);
    private ConcurrentMessageListenerContainer container;
    private String username;
    private PricesRepository pricesRepository;

    public KafkaPriceConsumer(String username, PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
        this.username = username;
    }

    public ConcurrentMessageListenerContainer getContainer() {
        return container;
    }

//    public List<String> getResults() {
//        return results;
//    }
//
//    private List<String> results = new ArrayList<>();

    public void consume() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> consumerConfig = Map.of(
                BOOTSTRAP_SERVERS_CONFIG, Configuration.kafkaServer,
                GROUP_ID_CONFIG, "test-consumer-group"
        );

        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerConfig,
                        new StringDeserializer(),
                        new StringDeserializer());

        ContainerProperties containerProperties = new ContainerProperties(username);
        containerProperties.setMessageListener((MessageListener<String, String>) record -> {
            System.out.println(record.value());
            List<Prices> pricesList = gson.fromJson(record.value(),new TypeToken<List<Prices>>(){}.getType());
            for(Prices prices : pricesList){
                Prices oldPrice = pricesRepository.findByBookId(prices.getBookId());
                if(oldPrice!= null){
                    oldPrice.setPriceListings(prices.getPriceListings());
                    oldPrice.setBookTitle(prices.getBookTitle());
                    pricesRepository.saveAndFlush(oldPrice);
                }else{
                    pricesRepository.saveAndFlush(prices);
                }
            }
        });

        container = new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
        container.start();
        System.out.println("Consumed message:" + username);
        logger.info("&&& Message [{}] consumed", username);
    }

}
