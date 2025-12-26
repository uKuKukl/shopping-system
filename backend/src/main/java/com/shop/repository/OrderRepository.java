package com.shop.repository;

import com.shop.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByOrderNo(String orderNo);
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
