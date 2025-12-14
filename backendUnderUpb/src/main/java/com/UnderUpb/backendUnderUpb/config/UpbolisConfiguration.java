package com.UnderUpb.backendUnderUpb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UpbolisConfiguration {

    @Bean
    public RestTemplate restTemplate(@Value("${upbolis.timeout-seconds:5}") int timeoutSeconds) {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(timeoutSeconds * 1000);
        rf.setReadTimeout(timeoutSeconds * 1000);
        return new RestTemplate(rf);
    }
}
