package com.billit.investment.connect.user.client;

import com.billit.investment.config.FeignConfig;
import com.billit.investment.connect.user.dto.UserServiceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8085", configuration = FeignConfig.class)
public interface UserServiceClient {
    @PostMapping("/api/v1/user-service/accounts/transaction/invest/deposit")
    boolean depositToAccount(@RequestParam Long userId,
                             @RequestBody UserServiceRequestDto request);

    @PostMapping("/api/v1/user_service/accounts/transaction/invest/withdraw")
    ResponseEntity<String> withdrawInvest(@RequestParam Long userId,
                                          @RequestBody UserServiceRequestDto request);

}
