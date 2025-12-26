package com.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySalesReport {
    private Long categoryId;
    private String categoryName;
    private Long totalQty;
    private BigDecimal totalAmount;
}
