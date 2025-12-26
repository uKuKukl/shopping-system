package com.shop.dto.admin;

import com.shop.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotBlank(message = "SKU不能为空")
    private String sku;
    @NotBlank(message = "商品名不能为空")
    private String name;
    private String description;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    @NotNull(message = "库存不能为空")
    private Integer stock;
    private ProductStatus status;
}
