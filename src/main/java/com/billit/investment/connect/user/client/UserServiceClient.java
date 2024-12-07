package com.billit.investment.connect.user.client;

import com.billit.investment.config.FeignConfig;
import com.billit.investment.connect.user.dto.UserServiceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "USER-SERVICE",
        configuration = FeignConfig.class,
        path = "/api/v1/user-service",
        url = "${feign.client.config.user-service.url}")
public interface UserServiceClient {
    @PostMapping("/accounts/transaction/invest/deposit")
    ResponseEntity<String> depositToAccount(@RequestParam UUID userId,
                             @RequestBody UserServiceRequestDto request);

    @PostMapping("/accounts/transaction/invest/withdraw")
    ResponseEntity<String> withdrawInvest(@RequestParam UUID userId,
                                          @RequestBody UserServiceRequestDto request);

}
