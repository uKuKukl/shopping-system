package com.shop.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemView {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private Boolean checked;
}
