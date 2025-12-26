package com.shop.controller;

import com.shop.common.ApiResponse;
import com.shop.dto.order.CreateOrderRequest;
import com.shop.dto.order.OrderDetail;
import com.shop.dto.order.OrderSummary;
import com.shop.service.OrderService;
import com.shop.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderSummary> create(@Valid @RequestBody CreateOrderRequest request) {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ApiResponse<List<OrderSummary>> list() {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(orderService.listOrders(userId));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<OrderDetail> detail(@PathVariable String orderNo) {
        Long userId = SecurityUtils.currentUserId();
        return ApiResponse.success(orderService.getOrderDetail(userId, orderNo));
    }

    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String orderNo) {
        Long userId = SecurityUtils.currentUserId();
        orderService.cancelOrder(userId, orderNo);
        return ApiResponse.success(null);
    }
}
