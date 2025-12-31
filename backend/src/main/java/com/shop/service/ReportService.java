package com.shop.service;

import com.shop.dto.admin.CategorySalesReport;
import com.shop.dto.admin.DailyHotProductReport;
import com.shop.dto.admin.DailySalesDetailReport;
import com.shop.dto.admin.DailySalesReport;
import com.shop.dto.admin.ProductSalesReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
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

    public List<DailyHotProductReport> dailyHotProducts(int topN) {
        String sql = """
                select paid_date, product_id, product_name, total_qty, total_amount
                from (
                    select
                        date(o.paid_at) as paid_date,
                        p.id as product_id,
                        p.name as product_name,
                        sum(oi.quantity) as total_qty,
                        sum(oi.subtotal) as total_amount,
                        row_number() over (partition by date(o.paid_at) order by sum(oi.quantity) desc) as rn
                    from order_items oi
                    join orders o on o.id = oi.order_id
                    join products p on p.id = oi.product_id
                    where o.status in ('PAID','SHIPPED','COMPLETED') and o.paid_at is not null
                    group by paid_date, product_id, product_name
                ) t
                where t.rn <= :topN
                order by t.paid_date desc, t.total_qty desc
                """;
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("topN", topN)
                .getResultList();
        List<DailyHotProductReport> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new DailyHotProductReport(((java.sql.Date) row[0]).toLocalDate(),
                    ((Number) row[1]).longValue(),
                    (String) row[2],
                    ((Number) row[3]).longValue(),
                    (BigDecimal) row[4]));
        }
        return result;
    }

    public List<DailySalesDetailReport> dailySalesDetail(LocalDate date) {
        String sql = """
                select
                    date(o.paid_at) as paid_date,
                    p.id as product_id,
                    p.name as product_name,
                    sum(oi.quantity) as total_qty,
                    sum(oi.subtotal) as total_amount,
                    count(distinct o.id) as order_count
                from order_items oi
                join orders o on o.id = oi.order_id
                join products p on p.id = oi.product_id
                where o.status in ('PAID','SHIPPED','COMPLETED') and o.paid_at is not null
                  and (:paidDate is null or date(o.paid_at) = :paidDate)
                group by paid_date, product_id, product_name
                order by paid_date desc, total_amount desc
                """;
        var query = entityManager.createNativeQuery(sql);
        query.setParameter("paidDate", date == null ? null : Date.valueOf(date));
        List<Object[]> rows = query.getResultList();
        List<DailySalesDetailReport> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new DailySalesDetailReport(((Date) row[0]).toLocalDate(),
                    ((Number) row[1]).longValue(),
                    (String) row[2],
                    ((Number) row[3]).longValue(),
                    (BigDecimal) row[4],
                    ((Number) row[5]).longValue()));
        }
        return result;
    }
}
