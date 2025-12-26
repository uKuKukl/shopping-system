package com.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailySalesReport {
    private LocalDate paidDate;
    private Long orderCount;
    private BigDecimal totalAmount;
}
