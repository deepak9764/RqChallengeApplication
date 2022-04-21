package com.example.rqchallenge.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
@Slf4j
public class EmployeeContext {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

}
