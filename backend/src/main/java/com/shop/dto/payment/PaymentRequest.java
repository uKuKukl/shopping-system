package com.shop.dto.payment;

import com.shop.enums.PayChannel;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNo;
    private PayChannel payChannel;
    private String cardHolder;
    private String cardLast4;
    private String simulateResult;
}
