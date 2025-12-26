package com.shop.service;

import com.shop.dto.admin.CategorySalesReport;
import com.shop.dto.admin.DailySalesReport;
import com.shop.dto.admin.ProductSalesReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductSalesReport> productSales() {
        List<Object[]> rows = entityManager.createNativeQuery("select product_id, product_name, total_qty, total_amount from v_product_sales").getResultList();
        List<ProductSalesReport> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new ProductSalesReport(((Number) row[0]).longValue(), (String) row[1],
                    ((Number) row[2]).longValue(), (BigDecimal) row[3]));
        }
        return result;
    }

    public List<CategorySalesReport> categorySales() {
        List<Object[]> rows = entityManager.createNativeQuery("select category_id, category_name, total_qty, total_amount from v_category_sales").getResultList();
        List<CategorySalesReport> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new CategorySalesReport(((Number) row[0]).longValue(), (String) row[1],
                    ((Number) row[2]).longValue(), (BigDecimal) row[3]));
        }
        return result;
    }

    public List<DailySalesReport> dailySales() {
        List<Object[]> rows = entityManager.createNativeQuery("select paid_date, order_count, total_amount from v_daily_sales").getResultList();
        List<DailySalesReport> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new DailySalesReport(((java.sql.Date) row[0]).toLocalDate(),
                    ((Number) row[1]).longValue(), (BigDecimal) row[2]));
        }
        return result;
    }
}
