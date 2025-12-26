package com.shop.service;

import com.shop.common.BusinessException;
import com.shop.dto.cart.AddCartItemRequest;
import com.shop.dto.cart.CartItemView;
import com.shop.dto.cart.CartView;
import com.shop.dto.cart.UpdateCartItemRequest;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Product;
import com.shop.enums.ProductStatus;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            return cartRepository.save(cart);
        });
    }

    public CartView getCartView(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemView> views = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            CartItemView view = new CartItemView(item.getId(), product.getId(), product.getName(),
                    product.getPrice(), product.getStock(), item.getQuantity(), item.getChecked());
            views.add(view);
            if (Boolean.TRUE.equals(item.getChecked())) {
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return new CartView(cart.getId(), views, total);
    }

    @Transactional
    public CartView addItem(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在"));
        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException("商品已下架");
        }
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCartId(cart.getId());
                    newItem.setProductId(request.getProductId());
                    newItem.setQuantity(0);
                    newItem.setChecked(true);
                    return newItem;
                });
        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);
        return getCartView(userId);
    }

    @Transactional
    public CartView updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("购物车项不存在"));
        if (!item.getCartId().equals(cart.getId())) {
            throw new BusinessException("无权操作该购物车项");
        }
        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
        }
        if (request.getChecked() != null) {
            item.setChecked(request.getChecked());
        }
        cartItemRepository.save(item);
        return getCartView(userId);
    }

    @Transactional
    public CartView deleteItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("购物车项不存在"));
        if (!item.getCartId().equals(cart.getId())) {
            throw new BusinessException("无权操作该购物车项");
        }
        cartItemRepository.delete(item);
        return getCartView(userId);
    }
}
