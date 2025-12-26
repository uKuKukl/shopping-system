package com.shop.controller;

import com.shop.common.ApiResponse;
import com.shop.dto.catalog.ProductDetailResponse;
import com.shop.entity.Category;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import com.shop.enums.ProductStatus;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ProductImageRepository;
import com.shop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public CatalogController(CategoryRepository categoryRepository,
                             ProductRepository productRepository,
                             ProductImageRepository productImageRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.success(categoryRepository.findAll());
    }

    @GetMapping("/products")
    public ApiResponse<Page<Product>> products(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productRepository.findByStatus(ProductStatus.ON_SALE, PageRequest.of(page, size));
        return ApiResponse.success(result);
    }

    @GetMapping("/products/search")
    public ApiResponse<Page<Product>> search(@RequestParam("q") String q,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Page<Product> result = productRepository.search(ProductStatus.ON_SALE, q, PageRequest.of(page, size));
        return ApiResponse.success(result);
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(id);
        return ApiResponse.success(new ProductDetailResponse(product, images));
    }
}
