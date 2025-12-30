package com.shop.dto.payment;

import com.shop.enums.PayChannel;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNo;
    private PayChannel payChannel;
    private String cardHolder;
    @Pattern(regexp = "^$|^\\d{4}$", message = "卡号后四位必须是4位数字")
    private String cardLast4;
    private String simulateResult;
}
