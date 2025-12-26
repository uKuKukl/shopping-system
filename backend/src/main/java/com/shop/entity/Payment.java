package com.shop.entity;

import com.shop.enums.PayChannel;
import com.shop.enums.PayStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_channel", nullable = false)
    private PayChannel payChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false)
    private PayStatus payStatus;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "bank_txn_no")
    private String bankTxnNo;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "card_last4", length = 4, columnDefinition = "char(4)")
    private String cardLast4;

    @Column(name = "requested_at", insertable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "raw_payload", columnDefinition = "json")
    private String rawPayload;
}
