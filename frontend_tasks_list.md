You are Codex. Build the FRONTEND using Vue 3 + Vite.
Backend baseURL: http://localhost:8080
IMPORTANT:
- Use vue-router + pinia + axios + Element Plus.
- Payment is PSEUDO PAYMENT: call our backend only; no real bank/third-party integration.

============================
FRONTEND TASK LIST (IN ORDER)
============================

A. Bootstrap
1) Create Vue3 + Vite project under frontend/. [x]
2) Install: vue-router, pinia, axios, element-plus. [x]
3) Layout: nav (Home, Cart, Orders, Admin if ADMIN), login/logout. [x]

B. Axios
4) axios instance: [x]
   - baseURL http://localhost:8080
   - attach Authorization: Bearer token
   - handle 401 -> logout + redirect /login
   - show error message if ApiResponse.code != 0

C. Stores
5) auth store: token + user(role), login/logout, persist localStorage. [x]
6) cart store: fetchCart/add/update/delete. [x]

D. Pages
7) /login: call POST /api/auth/login, store token+user. [x]
8) /: categories + products + search. [x]
9) /product/:id: detail + add to cart. [x]
10) /cart: list + modify qty/checked + totals + go checkout. [x]
11) /checkout: receiver form -> POST /api/orders -> redirect /pay/:orderNo [x]
12) /pay/:orderNo (PSEUDO PAYMENT PAGE): [x]
    - load order: GET /api/orders/{orderNo}
    - payment form:
      payChannel (MOCK/BANK), cardHolder, cardLast4,
      optional toggle "simulate fail" -> send simulateResult="FAILED"
    - on pay click:
      POST /api/payments
      if SUCCESS -> show success and redirect order detail
      if FAILED -> show failure and allow retry
    - No external payment links/QR codes.
13) /orders and /orders/:orderNo: show list + detail; cancel if PENDING_PAY. [x]

E. Admin routes
14) route guard for /admin/** requires user.role == ADMIN. [x]
15) /admin/products /admin/orders /admin/reports call corresponding admin APIs. [x]

F. Docs
16) docs/run-frontend.md + update README [x]

Acceptance test:
- User can go through: browse -> add cart -> checkout -> pay (success) -> order shows PAID.
- simulate fail shows failed message and order remains PENDING_PAY.
