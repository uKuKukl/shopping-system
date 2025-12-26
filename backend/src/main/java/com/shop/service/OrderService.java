package com.shop.service;

import com.shop.common.BusinessException;
import com.shop.dto.order.CreateOrderRequest;
import com.shop.dto.order.OrderDetail;
import com.shop.dto.order.OrderItemView;
import com.shop.dto.order.OrderSummary;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.OrderEntity;
import com.shop.entity.OrderItem;
import com.shop.entity.Product;
import com.shop.enums.OrderStatus;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderSummary createOrder(Long userId, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("购物车为空"));
        List<CartItem> checkedItems = cartItemRepository.findByCartIdAndCheckedTrue(cart.getId());
        if (checkedItems.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : checkedItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            int affected = productRepository.decrementStock(product.getId(), item.getQuantity());
            if (affected == 0) {
                throw new BusinessException("库存不足");
            }
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        OrderEntity order = new OrderEntity();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAY);
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setCurrency("CNY");
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }

        cartItemRepository.deleteByCartIdAndCheckedTrue(cart.getId());
        List<OrderItemView> itemViews = orderItems.stream()
                .map(item -> new OrderItemView(item.getId(), item.getProductId(), item.getProductName(),
                        item.getUnitPrice(), item.getQuantity(), item.getSubtotal()))
                .toList();
        return new OrderSummary(order.getOrderNo(), order.getStatus(), order.getTotalAmount(), order.getPayAmount(),
                order.getCurrency(), order.getCreatedAt(), order.getPaidAt(), itemViews);
    }

    public List<OrderSummary> listOrders(Long userId) {
        List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<OrderSummary> summaries = new ArrayList<>();
        for (OrderEntity order : orders) {
            List<OrderItemView> items = orderItemRepository.findByOrderId(order.getId())
                    .stream()
                    .map(item -> new OrderItemView(item.getId(), item.getProductId(), item.getProductName(),
                            item.getUnitPrice(), item.getQuantity(), item.getSubtotal()))
                    .toList();
            summaries.add(new OrderSummary(order.getOrderNo(), order.getStatus(), order.getTotalAmount(),
                    order.getPayAmount(), order.getCurrency(), order.getCreatedAt(), order.getPaidAt(), items));
        }
        return summaries;
    }

    public OrderDetail getOrderDetail(Long userId, String orderNo) {
        OrderEntity order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }
        List<OrderItemView> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(item -> new OrderItemView(item.getId(), item.getProductId(), item.getProductName(),
                        item.getUnitPrice(), item.getQuantity(), item.getSubtotal()))
                .toList();
        return new OrderDetail(order.getOrderNo(), order.getStatus(), order.getTotalAmount(), order.getPayAmount(),
                order.getCurrency(), order.getReceiverName(), order.getReceiverPhone(), order.getReceiverAddress(),
                order.getCreatedAt(), order.getPaidAt(), order.getCancelledAt(), items);
    }

    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        OrderEntity order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权取消该订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY) {
            throw new BusinessException("订单状态不允许取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            productRepository.incrementStock(item.getProductId(), item.getQuantity());
        }
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ON" + time + rand;
    }
}
