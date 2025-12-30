package com.shop.config;

import com.shop.entity.Category;
import com.shop.entity.OrderEntity;
import com.shop.entity.OrderItem;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import com.shop.enums.ProductStatus;
import com.shop.enums.OrderStatus;
import com.shop.entity.User;
import com.shop.enums.UserRole;
import com.shop.enums.UserStatus;
import com.shop.repository.CategoryRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductImageRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductImageRepository productImageRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ensureAdminUser();
        List<Product> products = ensureProducts();
        if (orderRepository.count() == 0 && !products.isEmpty()) {
            seedOrders(products);
        }
    }

    private void ensureAdminUser() {
        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@shop.com");
            admin.setPhone("13900000000");
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            return userRepository.save(admin);
        });
    }

    private List<Product> ensureProducts() {
        if (productRepository.count() > 0) {
            return productRepository.findAll();
        }

        List<Category> categories = new ArrayList<>(categoryRepository.findAll());
        if (categories.isEmpty()) {
            categories.add(saveCategory("家居", 1));
            categories.add(saveCategory("数码", 2));
            categories.add(saveCategory("厨房", 3));
        }

        Product p1 = buildProduct(categories.get(0).getId(), "HOME-1001", "暖光香薰灯",
                "柔和氛围灯，适合卧室与客厅使用。", new BigDecimal("129.00"), 50);
        Product p2 = buildProduct(categories.get(1).getId(), "TECH-2001", "无线降噪耳机",
                "主动降噪，续航持久，适合通勤。", new BigDecimal("699.00"), 30);
        Product p3 = buildProduct(categories.get(2).getId(), "KITCH-3001", "不粘煎锅 28cm",
                "快速导热，易清洁。", new BigDecimal("159.00"), 80);
        Product p4 = buildProduct(categories.get(1).getId(), "TECH-2002", "便携充电宝 20000mAh",
                "双口输出，支持快充。", new BigDecimal("189.00"), 120);
        Product p5 = buildProduct(categories.get(0).getId(), "HOME-1002", "软云抱枕",
                "亲肤面料，支撑力好。", new BigDecimal("79.00"), 100);

        productRepository.saveAll(List.of(p1, p2, p3, p4, p5));

        addImage(p1, "https://picsum.photos/seed/home1001/800/600");
        addImage(p2, "https://picsum.photos/seed/tech2001/800/600");
        addImage(p3, "https://picsum.photos/seed/kitch3001/800/600");
        addImage(p4, "https://picsum.photos/seed/tech2002/800/600");
        addImage(p5, "https://picsum.photos/seed/home1002/800/600");
        return List.of(p1, p2, p3, p4, p5);
    }

    private void seedOrders(List<Product> products) {
        User user = userRepository.findByUsername("demo")
                .orElseGet(() -> {
                    User demo = new User();
                    demo.setUsername("demo");
                    demo.setEmail("demo@shop.com");
                    demo.setPhone("13800000000");
                    demo.setPasswordHash(passwordEncoder.encode("123456"));
                    demo.setRole(UserRole.CUSTOMER);
                    demo.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(demo);
                });

        createOrder(user, products, List.of(new ItemSpec(0, 2), new ItemSpec(1, 1)),
                OrderStatus.PAID, LocalDateTime.now().minusDays(1));
        createOrder(user, products, List.of(new ItemSpec(2, 1)),
                OrderStatus.SHIPPED, LocalDateTime.now().minusDays(2));
        createOrder(user, products, List.of(new ItemSpec(3, 1), new ItemSpec(4, 3)),
                OrderStatus.COMPLETED, LocalDateTime.now().minusDays(3));
    }

    private void createOrder(User user, List<Product> products, List<ItemSpec> specs,
                             OrderStatus status, LocalDateTime paidAt) {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemSpec spec : specs) {
            if (spec.index < 0 || spec.index >= products.size()) {
                continue;
            }
            Product product = products.get(spec.index);
            int available = product.getStock() == null ? 0 : product.getStock();
            int qty = Math.min(spec.qty, available);
            if (qty <= 0) {
                continue;
            }
            product.setStock(available - qty);
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(qty);
            item.setSubtotal(subtotal);
            items.add(item);
        }

        if (items.isEmpty()) {
            return;
        }

        OrderEntity order = new OrderEntity();
        order.setOrderNo("SEED" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999));
        order.setUserId(user.getId());
        order.setStatus(status);
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setCurrency("CNY");
        order.setReceiverName("演示用户");
        order.setReceiverPhone("13800000000");
        order.setReceiverAddress("示例地址 88 号");
        if (status == OrderStatus.PAID || status == OrderStatus.SHIPPED || status == OrderStatus.COMPLETED) {
            order.setPaidAt(paidAt);
        }
        orderRepository.save(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }
    }

    private static class ItemSpec {
        private final int index;
        private final int qty;

        private ItemSpec(int index, int qty) {
            this.index = index;
            this.qty = qty;
        }
    }

    private Category saveCategory(String name, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setSortOrder(sortOrder);
        return categoryRepository.save(category);
    }

    private Product buildProduct(Long categoryId, String sku, String name, String description,
                                 BigDecimal price, int stock) {
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(ProductStatus.ON_SALE);
        return product;
    }

    private void addImage(Product product, String url) {
        ProductImage image = new ProductImage();
        image.setProductId(product.getId());
        image.setImageUrl(url);
        image.setIsPrimary(true);
        image.setSortOrder(1);
        productImageRepository.save(image);
    }
}
