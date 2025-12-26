package com.shop.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    private Boolean checked;
}
