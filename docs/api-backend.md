# Backend API

Base URL: `http://localhost:8080`

## Health
- GET `/api/health`

## Auth
- POST `/api/auth/register`
- POST `/api/auth/login`

## Catalog
- GET `/api/categories`
- GET `/api/products`
- GET `/api/products/search?q=`
- GET `/api/products/{id}`

## Cart (Auth)
- GET `/api/cart`
- POST `/api/cart/items`
- PATCH `/api/cart/items/{itemId}`
- DELETE `/api/cart/items/{itemId}`

## Orders (Auth)
- POST `/api/orders`
- GET `/api/orders`
- GET `/api/orders/{orderNo}`
- POST `/api/orders/{orderNo}/cancel`

## Payments (Auth)
- POST `/api/payments`
  - body: `{ orderNo, payChannel, cardHolder?, cardLast4?, simulateResult? }`

## Admin (ADMIN)
- Products: `/api/admin/products`
- Orders: `/api/admin/orders`
- Reports:
  - `/api/admin/reports/product-sales`
  - `/api/admin/reports/category-sales`
  - `/api/admin/reports/daily-sales`
  - `/api/admin/reports/daily-hot-products?topN=3`
  - `/api/admin/reports/daily-sales-detail?date=YYYY-MM-DD`
