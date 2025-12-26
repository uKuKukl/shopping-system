package com.shop.dto.payment;

import com.shop.enums.PayStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String orderNo;
    private PayStatus payStatus;
    private BigDecimal amount;
    private String message;
}
