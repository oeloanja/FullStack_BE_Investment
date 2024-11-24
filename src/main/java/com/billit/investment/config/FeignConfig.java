package com.billit.investment.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Name", "investment-service");
            System.out.println("Adding header: " + requestTemplate.headers());  // 로그 추가
        };
    }
}