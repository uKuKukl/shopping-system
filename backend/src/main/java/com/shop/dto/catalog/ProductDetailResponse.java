package com.shop.dto.catalog;

import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailResponse {
    private Product product;
    private List<ProductImage> images;
}
