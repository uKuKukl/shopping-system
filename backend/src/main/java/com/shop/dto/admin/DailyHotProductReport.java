package com.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyHotProductReport {
    private LocalDate paidDate;
    private Long productId;
    private String productName;
    private Long totalQty;
    private BigDecimal totalAmount;
}
