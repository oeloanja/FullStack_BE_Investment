package com.billit.investment.connect.loan_group.client;

import com.billit.investment.connect.loan_group.dto.LoanGroupRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "loan-group-service", url = "localhost:8084")
public interface LoanGroupServiceClient {
    @PutMapping("/api/v1/loan_group/account/invest")
    ResponseEntity<String> updatePlatformAccountBalance(
            @RequestBody LoanGroupRequestDto request
            );
}
