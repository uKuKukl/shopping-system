package com.shop.dto.order;

import com.shop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderSummary {
    private String orderNo;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<OrderItemView> items;
}
