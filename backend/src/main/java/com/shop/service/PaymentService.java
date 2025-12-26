package com.shop.service;

import com.shop.common.BusinessException;
import com.shop.dto.payment.PaymentRequest;
import com.shop.dto.payment.PaymentResponse;
import com.shop.entity.OrderEntity;
import com.shop.entity.Payment;
import com.shop.enums.OrderStatus;
import com.shop.enums.PayChannel;
import com.shop.enums.PayStatus;
import com.shop.repository.OrderRepository;
import com.shop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        OrderEntity order = orderRepository.findByOrderNo(request.getOrderNo())
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() == OrderStatus.PAID) {
            return new PaymentResponse(order.getOrderNo(), PayStatus.SUCCESS, order.getPayAmount(), "已支付");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY) {
            throw new BusinessException("订单状态不允许支付");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPayChannel(request.getPayChannel() == null ? PayChannel.MOCK : request.getPayChannel());
        payment.setPayStatus(PayStatus.INIT);
        payment.setAmount(order.getPayAmount());
        payment.setCardHolder(request.getCardHolder());
        payment.setCardLast4(request.getCardLast4());
        payment.setRawPayload("{\"simulateResult\":\"" +
                (request.getSimulateResult() == null ? "SUCCESS" : request.getSimulateResult()) + "\"}");
        paymentRepository.save(payment);

        boolean success = request.getSimulateResult() == null || !"FAILED".equalsIgnoreCase(request.getSimulateResult());
        if (success) {
            payment.setPayStatus(PayStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setBankTxnNo(UUID.randomUUID().toString().replace("-", ""));
            paymentRepository.save(payment);

            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);
            return new PaymentResponse(order.getOrderNo(), PayStatus.SUCCESS, order.getPayAmount(), "支付成功");
        } else {
            payment.setPayStatus(PayStatus.FAILED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            return new PaymentResponse(order.getOrderNo(), PayStatus.FAILED, order.getPayAmount(), "支付失败");
        }
    }

    public List<Payment> listByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
