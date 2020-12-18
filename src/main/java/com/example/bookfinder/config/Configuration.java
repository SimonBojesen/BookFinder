package com.example.bookfinder.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Configuration {
    private static final String localhost = "localhost";
    private static final String docker = "host.docker.internal";

    public static final String googleApiKey = "AIzaSyAlU87x4nTCbHZWWKpUQCicuFUXG-lAq9s";
    public static final String bookReviewApi = "http://" + localhost + ":9010";
    public static final String bookAlert = "http://" + localhost + ":9000";
    public static final String kafkaServer = localhost + ":9092";

}
