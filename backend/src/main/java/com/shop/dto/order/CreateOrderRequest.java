package com.shop.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "收货人不能为空")
    private String receiverName;
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
}
