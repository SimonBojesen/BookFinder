package com.example.bookfinder.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;

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

    public KafkaPriceConsumer(String username){
        this.username = username;
    }

    public ConcurrentMessageListenerContainer getContainer() {
        return container;
    }

    public List<String> getResults() {
        return results;
    }

    private List<String> results = new ArrayList<>();

    public void consume() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> consumerConfig = Map.of(
                BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
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
            results.add(record.value());
        });

        container = new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);

        container.start();
        System.out.println("Consumed message:" + username);
        logger.info("&&& Message [{}] consumed", username);
    }

}
