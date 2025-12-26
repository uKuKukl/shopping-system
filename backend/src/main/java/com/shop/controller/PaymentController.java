package com.shop.controller;

import com.shop.common.ApiResponse;
import com.shop.dto.payment.PaymentRequest;
import com.shop.dto.payment.PaymentResponse;
import com.shop.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ApiResponse<PaymentResponse> pay(@RequestBody PaymentRequest request) {
        return ApiResponse.success(paymentService.pay(request));
    }
}
