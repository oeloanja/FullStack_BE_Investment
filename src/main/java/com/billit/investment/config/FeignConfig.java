package com.billit.investment.config;

import feign.Client;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Name", "investment-service");

            log.info("Final Request Headers: {}", requestTemplate.headers());
            log.info("Request URL: {}", requestTemplate.url());
        };
    }
    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }
}