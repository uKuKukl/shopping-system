package com.shop.controller.admin;

import com.shop.common.ApiResponse;
import com.shop.common.BusinessException;
import com.shop.dto.admin.ProductImageRequest;
import com.shop.dto.admin.ProductRequest;
import com.shop.dto.admin.UpdateStatusRequest;
import com.shop.dto.admin.UpdateStockRequest;
import com.shop.dto.admin.UpdateProductImageRequest;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import com.shop.enums.ProductStatus;
import com.shop.repository.ProductImageRepository;
import com.shop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public AdminProductController(ProductRepository productRepository,
                                  ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @GetMapping
    public ApiResponse<Page<Product>> list(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) ProductStatus status) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> result = status == null
                ? productRepository.findAll(pageable)
                : productRepository.findByStatus(status, pageable);
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<Product> create(@Valid @RequestBody ProductRequest request) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus() == null ? ProductStatus.ON_SALE : request.getStatus());
        return ApiResponse.success(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        product.setCategoryId(request.getCategoryId());
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus() == null ? product.getStatus() : request.getStatus());
        return ApiResponse.success(productRepository.save(product));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Product> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        if (request.getStatus() == null) {
            throw new BusinessException("状态不能为空");
        }
        product.setStatus(request.getStatus());
        return ApiResponse.success(productRepository.save(product));
    }

    @PatchMapping("/{id}/stock")
    public ApiResponse<Product> updateStock(@PathVariable Long id, @RequestBody UpdateStockRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        if (request.getStock() == null) {
            throw new BusinessException("库存不能为空");
        }
        product.setStock(request.getStock());
        return ApiResponse.success(productRepository.save(product));
    }

    @GetMapping("/{id}/images")
    public ApiResponse<List<ProductImage>> listImages(@PathVariable Long id) {
        productRepository.findById(id).orElseThrow(() -> new BusinessException("商品不存在"));
        return ApiResponse.success(productImageRepository.findByProductIdOrderBySortOrderAsc(id));
    }

    @PostMapping("/{id}/images")
    public ApiResponse<ProductImage> addImage(@PathVariable Long id, @RequestBody ProductImageRequest request) {
        productRepository.findById(id).orElseThrow(() -> new BusinessException("商品不存在"));
        if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
            throw new BusinessException("图片地址不能为空");
        }
        ProductImage image = new ProductImage();
        image.setProductId(id);
        image.setImageUrl(request.getImageUrl());
        image.setIsPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        image.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        ProductImage saved = productImageRepository.save(image);
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            resetPrimary(id, saved.getId());
        }
        return ApiResponse.success(saved);
    }

    @PatchMapping("/images/{imageId}")
    public ApiResponse<ProductImage> updateImage(@PathVariable Long imageId,
                                                 @RequestBody UpdateProductImageRequest request) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("图片不存在"));
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            image.setImageUrl(request.getImageUrl());
        }
        if (request.getSortOrder() != null) {
            image.setSortOrder(request.getSortOrder());
        }
        if (request.getIsPrimary() != null) {
            image.setIsPrimary(request.getIsPrimary());
        }
        ProductImage saved = productImageRepository.save(image);
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            resetPrimary(image.getProductId(), saved.getId());
        }
        return ApiResponse.success(saved);
    }

    @DeleteMapping("/images/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("图片不存在"));
        productImageRepository.delete(image);
        return ApiResponse.success(null);
    }

    private void resetPrimary(Long productId, Long primaryId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(productId);
        for (ProductImage img : images) {
            boolean isPrimary = img.getId().equals(primaryId);
            if (img.getIsPrimary() == null || img.getIsPrimary() != isPrimary) {
                img.setIsPrimary(isPrimary);
                productImageRepository.save(img);
            }
        }
    }
}
