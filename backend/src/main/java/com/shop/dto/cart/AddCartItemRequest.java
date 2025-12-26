package com.shop.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequest {
    @NotNull(message = "商品不能为空")
    private Long productId;

    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}
