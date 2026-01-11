package com.jugger.Gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${exchange.service.url}")
    private String exchangeServiceUrl;

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl(exchangeServiceUrl)
                .build();
    }
}
