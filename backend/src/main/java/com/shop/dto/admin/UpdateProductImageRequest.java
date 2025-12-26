package com.shop.dto.admin;

import lombok.Data;

@Data
public class UpdateProductImageRequest {
    private String imageUrl;
    private Boolean isPrimary;
    private Integer sortOrder;
}
