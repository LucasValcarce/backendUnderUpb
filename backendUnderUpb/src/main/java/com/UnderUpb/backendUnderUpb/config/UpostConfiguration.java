package com.UnderUpb.backendUnderUpb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class UpostConfiguration {

    @Bean(name = "upostRestTemplate")
    public RestTemplate upostRestTemplate(@Value("${upost.timeout-seconds:5}") int timeoutSeconds,
                                          RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(timeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
}
