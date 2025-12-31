package com.shop.controller.admin;

import com.shop.common.ApiResponse;
import com.shop.dto.admin.CategorySalesReport;
import com.shop.dto.admin.DailyHotProductReport;
import com.shop.dto.admin.DailySalesDetailReport;
import com.shop.dto.admin.DailySalesReport;
import com.shop.dto.admin.ProductSalesReport;
import com.shop.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/product-sales")
    public ApiResponse<List<ProductSalesReport>> productSales() {
        return ApiResponse.success(reportService.productSales());
    }

    @GetMapping("/category-sales")
    public ApiResponse<List<CategorySalesReport>> categorySales() {
        return ApiResponse.success(reportService.categorySales());
    }

    @GetMapping("/daily-sales")
    public ApiResponse<List<DailySalesReport>> dailySales() {
        return ApiResponse.success(reportService.dailySales());
    }

    @GetMapping("/daily-hot-products")
    public ApiResponse<List<DailyHotProductReport>> dailyHotProducts(@RequestParam(defaultValue = "3") int topN) {
        return ApiResponse.success(reportService.dailyHotProducts(topN));
    }

    @GetMapping("/daily-sales-detail")
    public ApiResponse<List<DailySalesDetailReport>> dailySalesDetail(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(reportService.dailySalesDetail(date));
    }
}
