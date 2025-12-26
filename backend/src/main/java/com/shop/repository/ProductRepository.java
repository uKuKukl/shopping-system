package com.shop.repository;

import com.shop.entity.Product;
import com.shop.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("select p from Product p where p.status = :status and (p.name like %:q% or p.description like %:q%)")
    Page<Product> search(@Param("status") ProductStatus status, @Param("q") String q, Pageable pageable);

    @Modifying
    @Query("update Product p set p.stock = p.stock - :qty where p.id = :pid and p.stock >= :qty")
    int decrementStock(@Param("pid") Long productId, @Param("qty") int qty);

    @Modifying
    @Query("update Product p set p.stock = p.stock + :qty where p.id = :pid")
    int incrementStock(@Param("pid") Long productId, @Param("qty") int qty);
}
