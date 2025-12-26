package com.shop.dto.admin;

import lombok.Data;

@Data
public class ProductImageRequest {
    private String imageUrl;
    private Boolean isPrimary;
    private Integer sortOrder;
}
