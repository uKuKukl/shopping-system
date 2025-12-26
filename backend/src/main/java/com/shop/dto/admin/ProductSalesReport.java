package com.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductSalesReport {
    private Long productId;
    private String productName;
    private Long totalQty;
    private BigDecimal totalAmount;
}
