package com.shop.controller;

import com.shop.common.ApiResponse;
import com.shop.dto.cart.AddCartItemRequest;
import com.shop.dto.cart.CartView;
import com.shop.dto.cart.UpdateCartItemRequest;
import com.shop.service.CartService;
import com.shop.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartView> getCart() {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(cartService.getCartView(userId));
    }

    @PostMapping("/items")
    public ApiResponse<CartView> addItem(@Valid @RequestBody AddCartItemRequest request) {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(cartService.addItem(userId, request));
    }

    @PatchMapping("/items/{itemId}")
    public ApiResponse<CartView> updateItem(@PathVariable Long itemId,
                                            @Valid @RequestBody UpdateCartItemRequest request) {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(cartService.updateItem(userId, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartView> deleteItem(@PathVariable Long itemId) {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(cartService.deleteItem(userId, itemId));
    }
}
