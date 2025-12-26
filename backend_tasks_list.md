You are Codex. Build the BACKEND of an Online Shopping System using Spring Boot 3 (Java 17) + MySQL 8.
IMPORTANT:
- Maven version is 3.9.9.
- Database tables ALREADY EXIST. DO NOT modify schema.
- Set spring.jpa.hibernate.ddl-auto=validate (or none). No schema changes.
- Map snake_case columns with @Column(name="...") exactly.
- Use BigDecimal for DECIMAL(10,2), LocalDateTime for DATETIME.
- Enums must match DB enum strings exactly.

Confirmed enums:
products.status: ON_SALE, OFF_SALE
orders.status: PENDING_PAY, PAID, SHIPPED, COMPLETED, CANCELLED
payments.pay_channel: BANK, ALIPAY, WECHAT, MOCK
payments.pay_status: INIT, SUCCESS, FAILED

PAYMENT REQUIREMENT (PSEUDO PAYMENT):
- Do NOT call any real bank/third-party payment API.
- Implement ONLY a simulated payment:
  backend writes payments record and updates orders status.
- Payment result can be simulated as SUCCESS by default; optionally allow a flag to simulate FAILED.

============================
BACKEND TASK LIST (IN ORDER)
============================

A. Project bootstrap (Maven 3.9.9)
1) Create Spring Boot 3 backend under backend/ using Maven 3.9.9 and Java 17. [x]
   Dependencies: web, validation, data-jpa, security, mysql-connector-j, lombok, jwt library.
2) Add GET /api/health returning ApiResponse. [x]

B. Config & common infra
3) application.yml: [x]
   - MySQL via env vars
   - ddl-auto=validate
   - show-sql true
   - server.port=8080
   - CORS allow http://localhost:5173
4) Implement ApiResponse<T> {code,message,data} and global exception handler (@RestControllerAdvice). [x]
5) BusinessException for domain errors (库存不足、订单状态不允许支付等). [x]

C. Enums
6) Create enums exactly: [x]
   ProductStatus {ON_SALE,OFF_SALE}
   OrderStatus {PENDING_PAY,PAID,SHIPPED,COMPLETED,CANCELLED}
   PayChannel {BANK,ALIPAY,WECHAT,MOCK}
   PayStatus {INIT,SUCCESS,FAILED}

D. Entities (map existing schema exactly)
7) Create entities mapping existing tables: [x]
   - Product -> products (exact columns per DDL provided)
   - OrderEntity -> orders (exact columns per DDL provided)
   - Payment -> payments (exact columns per DDL provided, raw_payload json mapped to String)
8) Create entities for users/categories/carts/cart_items/order_items using existing schema. [x]
   If ddl-auto=validate fails, STOP and print mismatch list and request exact DDL for those tables.

E. Repositories
9) Create JpaRepository for all entities. [x]
10) Add ProductRepository.decrementStock(pid, qty) as @Modifying JPQL: [x]
    update Product p set p.stock = p.stock - :qty
    where p.id = :pid and p.stock >= :qty
    return affected rows.

F. Auth + Security (JWT)
11) Implement register/login with BCrypt + JWT: [x]
    POST /api/auth/register
    POST /api/auth/login
12) Security rules: [x]
    permitAll: /api/health, /api/auth/**, GET /api/products/**, GET /api/categories
    authenticated: /api/cart/**, /api/orders/**, /api/payments/**
    admin-only: /api/admin/**

G. Catalog APIs
13) Implement: [x]
    GET /api/categories
    GET /api/products (only ON_SALE, pagination)
    GET /api/products/search?q=
    GET /api/products/{id}

H. Cart APIs
14) Implement: [x]
    GET /api/cart (auto-create)
    POST /api/cart/items
    PATCH /api/cart/items/{itemId}
    DELETE /api/cart/items/{itemId}

I. Orders (transactional checkout)
15) POST /api/orders (receiverName/Phone/Address): [x]
    - load checked cart items
    - @Transactional:
      decrement stock for each item (if any affectedRows==0 -> throw BusinessException("库存不足"))
      create OrderEntity status=PENDING_PAY, currency=CNY
      create order_items snapshot rows
      clear checked cart items
    - return orderNo + summary
16) My orders: [x]
    GET /api/orders
    GET /api/orders/{orderNo}
    POST /api/orders/{orderNo}/cancel (only PENDING_PAY; optional restore stock)

J. Payments (PSEUDO PAYMENT ONLY)
17) Implement POST /api/payments (authenticated): [x]
    Request body:
      { orderNo, payChannel, cardHolder?, cardLast4?, simulateResult? }
    Rules:
    - Do NOT call any external API.
    - Find order by orderNo.
    - If order.status == PAID -> idempotent success (return existing paid state).
    - If order.status != PENDING_PAY -> BusinessException("订单状态不允许支付")
    - Insert Payment with pay_status=INIT, amount=order.pay_amount, requested_at=now
    - Determine simulated result:
      - default SUCCESS (if simulateResult missing)
      - if simulateResult == "FAILED" then mark FAILED and DO NOT update order
    - On SUCCESS:
      - update payment: pay_status=SUCCESS, paid_at=now, bank_txn_no=random
      - update order: status=PAID, paid_at=now
    - On FAILED:
      - update payment: pay_status=FAILED, paid_at=NULL or now
      - order remains PENDING_PAY
    - Never store full card number (only last4).
    - raw_payload can store a small JSON string describing simulation.

K. Admin APIs
18) Admin product management CRUD + status + stock. [x]
19) Admin order query filters. [x]
20) Admin reports: [x]
    use existing views if present; otherwise compute via SQL group-by joins.

L. Docs
21) docs/run-backend.md + docs/api-backend.md [x]

Acceptance test:
- User login -> add to cart -> create order -> call /api/payments with default SUCCESS -> order becomes PAID.
- Call /api/payments again for same order -> idempotent success (no duplicate state change).
- simulateResult=FAILED keeps order PENDING_PAY and records FAILED payment.
