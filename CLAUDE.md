# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Memory Bank 使用规则

**必须先读 memory-bank/index.md 理解项目结构，再按需读对应 business/*.md 文件。**

不要直接盲读整个项目源码。

每次修改代码后必须更新对应 memory-bank 文件。新增模块时创建新 md + 更新 index.md。

## Project Overview

Second-hand bookstore platform — Spring Boot 2.7 backend (`books-shop-api/`) + Vue3 frontend (`books-vue/`). Two roles: regular users (browse, upload, cart, order, pay) and admins (manage books, users, orders, dashboard).

## Build & Run

```bash
# Backend (Java 8, Maven)
cd books-shop-api
mvn spring-boot:run                    # Start on http://localhost:8080/api
mvn test                                # Tests use H2 in-memory DB

# Frontend (Node 18+, Vite 8)
cd books-vue
npm install && npm run dev              # Start on http://localhost:5173
```

Default logins: `admin/123456`, `user1/123456`.

## Teaching Mode

When working with the user on this repo, follow "编程导师模式"：
- **Explain first**, then guide step-by-step. Do NOT write code without explaining the "why".
- Let the user make changes themselves when possible. Only write code directly when they ask or when the task is too mechanical.
- Focus on enterprise development patterns and interview-relevant knowledge.
- The goal is understanding, not just getting the feature done.

## Architecture

### Backend Layers

```
controller/  →  service/  →  mapper/  →  entity/  →  DB
     │                              │
     └── dto/ (request/response)     └── config/ (infrastructure)
```

- `Entity` (database-mapped) ≠ `VO` (frontend response) ≠ `DTO` (frontend request). Do NOT reuse them interchangeably.
- MyBatis-Plus uses `BaseMapper` (lambda queries, pagination, `@Version` for optimistic locking). There is no XML mapper except `mapper/*.xml`.
- MyBatis-Plus `@MapperScan("com.bookshop.mapper")` on `BooksShopApplication` means all mapper interfaces in that package are auto-registered. IDE's "cannot find bean" error for Mapper is a false positive.

### Config Classes

| Config | What It Customizes |
|--------|-------------------|
| `SecurityConfig` | JWT filter + URL rules + CORS + 401/403 JSON responses (no redirects) |
| `RedisCacheConfig` | JSON serialization, per-cache TTL with random offset (anti-avalanche) |
| `MybatisPlusConfig` | Pagination interceptor + `@Version` optimistic-lock interceptor |
| `RabbitMQConfig` | Direct exchange + durable queue for order payment events |
| `ElasticsearchConfig` | `RestTemplate` bean for ES 9.x REST API (Basic Auth + trust-all SSL) |
| `WebSocketConfig` | STOMP/SockJS broker on `/ws`, topic prefix `/topic` |

### Auth Flow

1. `AuthServiceImpl` → BCrypt verify → `JwtUtil.generateToken()` (HS256, claims: id/loginName/role, 24h expiry)
2. `JwtAuthenticationFilter` → extract `Bearer` token → parse → set `SecurityContextHolder`
3. URL-level rules in `SecurityConfig` (`.antMatchers("/users/**").hasRole("admin")`)
4. Method-level rules via `@PreAuthorize("hasRole('user')")` (requires `@EnableGlobalMethodSecurity`)

Role mapping: `admin` → `ROLE_admin`, `user` → `ROLE_user`

### Caching Strategy

Three-layer defense in `BookServiceImpl`:
- **Penetration**: Guava `BloomFilter` (pre-loaded with all book IDs on startup) → rejects unknown IDs before DB
- **Breakdown**: `@Cacheable(sync = true)` → single-threaded DB access per key
- **Avalanche**: Random TTL per cache name (`RedisCacheConfig`)

Write path: `@CacheEvict(allEntries = true)` clears all book caches on create/update/delete.

### ES Integration

- `BookSearchService` uses `RestTemplate` to call ES REST API — **not** the `elasticsearch-java` client (9.x HttpClient 5 conflicts with Spring Boot 2.7's HttpClient 4).
- On startup: creates index `books` (if not exists) → full-syncs all books from MySQL.
- On CRUD: `BookServiceImpl` calls `sync()`/`remove()` to keep ES consistent.
- `search()` uses `multi_match` across `bookName`, `bookAuthor`, `bookSn` fields.
- `GET /api/books/search?keyword=` endpoint in `BookController`.

### Inventory Locking

`Book.booksNum` is the stock field; `Book.version` has `@Version` annotation.
`deductStock()`: select → modify → updateById (MyBatis-Plus auto-adds `WHERE version=oldValue`). Retries 3 times on version conflict.

### RabbitMQ

`pay()` → update status → send to `order.exchange` (routing key `order.pay`) → `OrderPayConsumer` deducts stock.
Uses CloudAMQP — `virtual-host` in yml must equal username.

### WebSocket

Push targets: `/topic/orders/{userId}` (per-user) and `/topic/admin/orders` (admin broadcast).
Frontend: `@stomp/stompjs` + `sockjs-client` connect to `http://localhost:8080/api/ws`.

## Known Gotchas

- **ES won't work if C drive > 90% full**: ES's disk watermark prevents shard allocation. Workaround added to `elasticsearch.yml`: `cluster.routing.allocation.disk.threshold_enabled: false`.
- **Frontend requires `window.global = window`** in `index.html` (sockjs polyfill for Vite).
- **Bloom filter is in-memory only**: re-initialized from DB on restart.
- **No Vite proxy**: frontend calls `http://localhost:8080/api` directly — CORS in `SecurityConfig` is required.
- **Knife4j 3.0.3** API docs at `/api/doc.html` (not 4.x — version compatibility with Spring Boot 2.x).
- **application.yml contains real credentials** for MySQL, Redis (Upstash), RabbitMQ (CloudAMQP). Do not share this file externally.
