package com.shop.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartView {
    private Long cartId;
    private List<CartItemView> items;
    private BigDecimal totalAmount;
}
