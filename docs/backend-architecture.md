# Backend Architecture

## Overview

The IMS (Inventory Management System) backend is a Spring Boot 3.3.5 / Java 21 application built on **Hexagonal Architecture** (Ports & Adapters). It is structured as a Maven multi-module project where each module corresponds to an architectural layer. Data is stored in PostgreSQL, schema migrations are managed by Flyway, and domain events are published reliably via an Outbox pattern over Kafka.

---

## Module Structure

```
backend/
├── ims-domain/          # Core domain — models, value objects, events, exceptions, port contracts
├── ims-application/     # Use cases, commands, queries, inbound port interfaces, application DTOs
├── ims-infrastructure/  # JPA entities, repository adapters, Outbox publisher, AOP, data init
├── ims-api/             # REST controllers, request/response DTOs, JWT security, CORS
└── ims-bootstrap/       # Spring Boot entry point — wires all modules together
```

Dependencies flow inward only:

```
ims-bootstrap
    └── ims-api          → ims-application → ims-domain
    └── ims-infrastructure → ims-application → ims-domain
```

`ims-domain` has no Spring or infrastructure dependencies. `ims-application` depends only on Spring Context and TX. The outer modules (infrastructure, api) adapt to the inner contracts.

---

## Architectural Layers

### 1. Domain Layer (`ims-domain`)

The innermost layer. Contains business logic with no framework dependencies.

#### Domain Models

| Model | Key Fields | Key Behaviour |
|---|---|---|
| `Item` | id, userId, sku, name, description, category, defaultPrice, storagePosition, totalStorageStock, createdAt, updatedAt | `adjustStock(delta)`, `update(...)` |
| `Market` | id, userId, name, place, openDate, closeDate, status, createdAt | `open()`, `close()`, `isOpen()`, `isScheduled()`, `isClosed()` |
| `MarketItem` | id, marketId, itemId, allocatedStock, currentStock, marketPrice | `addStock()`, `increment()`, `decrement()`, `setMarketPrice()` |
| `Category` | id, userId, name, createdAt | — |
| `Transaction` | id, userId, marketId, itemId, type, quantityDelta, stockBefore, stockAfter, note, occurredAt, createdBy, salePrice, saleCurrency | Immutable; created via `create()` or `createSale()` |
| `User` | id, email, password, role, createdAt | `create(email, encodedPassword, role)` |

**Business rules enforced in domain:**
- `Market.open()` throws `InvalidMarketStateException` if not SCHEDULED
- `Market.close()` throws `InvalidMarketStateException` if not OPEN
- `MarketItem.decrement()` throws `InsufficientStockException` if result would go negative
- `StockLevel` and `Money` reject negative values on construction

#### Value Objects

| Value Object | Fields | Validations |
|---|---|---|
| `Money` | amount (BigDecimal), currency (ISO 4217) | Non-negative, non-null, 3-letter currency |
| `StockLevel` | quantity (int) | Non-negative |
| `StoragePosition` | zone, shelf, row, column | Non-blank strings, non-negative ints |
| `DateRange` | from (LocalDate), to (LocalDate) | `from` must not be after `to` |

#### Domain Events

All events extend `AbstractDomainEvent` which auto-generates `eventId` and `occurredAt`.

| Event | Aggregate | Fields |
|---|---|---|
| `ItemCreatedEvent` | Item | sku, name |
| `ItemStockAdjustedEvent` | Item | delta, newQuantity |
| `ItemShiftedToMarketEvent` | Market | marketId, itemId, quantity |
| `MarketOpenedEvent` | Market | marketName |
| `MarketClosedEvent` | Market | marketName |
| `MarketStockIncrementedEvent` | Market | itemId, quantity |
| `MarketStockDecrementedEvent` | Market | itemId, quantity |

#### Domain Exceptions

| Exception | When Thrown |
|---|---|
| `DuplicateSkuException` | Registering an item with a SKU already used by that user |
| `InsufficientStockException` | Decrementing stock below zero |
| `InvalidMarketStateException` | Invalid market state transition or shift to non-open/scheduled market |
| `ItemNotFoundException` | Item not found by ID or SKU |
| `MarketNotFoundException` | Market not found by ID |
| `MarketItemNotFoundException` | Market-item association not found |

#### Outbound Port Interfaces (Repository Contracts)

These interfaces live in `ims-domain` and are implemented by adapters in `ims-infrastructure`.

| Port | Methods |
|---|---|
| `ItemRepositoryPort` | save, findById, findBySkuAndUserId, findAllByUserId, deleteById |
| `MarketRepositoryPort` | save, findById, findAllByUserId, deleteById |
| `MarketItemRepositoryPort` | save, findByMarketIdAndItemId, findAllByMarketId, deleteByItemId |
| `CategoryRepositoryPort` | save, findAllByUserId, findByNameAndUserId, existsById, deleteById |
| `TransactionRepositoryPort` | save, findAllByUserId, findByMarketId, findByItemId, findByMarketIdAndItemId, deleteByItemId |
| `UserRepositoryPort` | save, findByEmail, existsByEmail |
| `DomainEventPublisherPort` | publish(DomainEvent) |

---

### 2. Application Layer (`ims-application`)

Orchestrates domain objects to fulfil use cases. Contains no HTTP or persistence code.

#### Inbound Ports (Interfaces consumed by `ims-api`)

**Command Ports:**

| Port | Operations |
|---|---|
| `ItemCommandPort` | registerItem, updateItem, adjustStorageStock, deleteItem |
| `MarketCommandPort` | createMarket, openMarket, closeMarket, updateMarket, deleteMarket |
| `MarketStockCommandPort` | shiftItem, incrementStock, decrementStock, setPrice |
| `CategoryCommandPort` | createCategory, deleteCategory |

**Query Ports:**

| Port | Operations |
|---|---|
| `ItemQueryPort` | getItem, listItems |
| `MarketQueryPort` | getMarket, listMarkets, getMarketSummary, getAllMarketsSummary |
| `MarketItemQueryPort` | getMarketItems, getMarketItem |
| `CategoryQueryPort` | listCategories(userId) |
| `TransactionQueryPort` | getTransactionHistory |
| `StorageSummaryPort` | getStorageSummary |

#### Commands (immutable records, carry userId for tenant isolation)

| Command | Fields |
|---|---|
| `RegisterItemCommand` | userId, sku, name, description, category, defaultPrice, currency, zone, shelf, row, column, initialStock |
| `UpdateItemCommand` | userId, itemId, name, description, category, defaultPrice, currency, zone, shelf, row, column |
| `AdjustStorageStockCommand` | userId, itemId, delta, note, createdBy |
| `CreateMarketCommand` | userId, name, place, openDate, closeDate |
| `UpdateMarketCommand` | userId, marketId, name, place, openDate, closeDate |
| `OpenMarketCommand` | userId, marketId |
| `CloseMarketCommand` | userId, marketId, createdBy |
| `ShiftItemCommand` | userId, marketId, itemId, quantity, marketPrice, currency, createdBy |
| `IncrementStockCommand` | userId, marketId, itemId, quantity, note, createdBy |
| `DecrementStockCommand` | userId, marketId, itemId, quantity, note, createdBy, salePrice, saleCurrency |
| `SetPriceCommand` | userId, marketId, itemId, price, currency |
| `CreateCategoryCommand` | userId, name |

#### Queries (immutable records)

| Query | Fields |
|---|---|
| `GetItemQuery` | userId, itemId |
| `ListItemsQuery` | userId, category (optional) |
| `GetMarketQuery` | userId, marketId |
| `ListMarketsQuery` | userId, status (optional) |
| `GetMarketSummaryQuery` | userId, marketId |
| `GetAllMarketsSummaryQuery` | userId, status (optional) |
| `GetStorageSummaryQuery` | userId |
| `GetTransactionHistoryQuery` | userId, marketId (optional), itemId (optional), page, size |

#### Use Cases

**Item:**

| Use Case | What it does |
|---|---|
| `RegisterItemUseCase` | Validates unique SKU per user, creates `Item`, optionally creates initial `STOCK_ADJUSTMENT` transaction, publishes `ItemCreatedEvent` |
| `UpdateItemUseCase` | Updates item metadata fields |
| `AdjustStorageStockUseCase` | Adjusts storage stock by signed delta, creates `STOCK_ADJUSTMENT` transaction, publishes `ItemStockAdjustedEvent` |
| `DeleteItemUseCase` | Deletes item and cascades to transactions and market items |
| `ItemQueryUseCase` | Reads item(s), supports optional category filter |

**Market:**

| Use Case | What it does |
|---|---|
| `CreateMarketUseCase` | Creates market in `SCHEDULED` state |
| `OpenMarketUseCase` | Validates `SCHEDULED → OPEN` transition, publishes `MarketOpenedEvent` |
| `CloseMarketUseCase` | Validates `OPEN → CLOSED` transition, returns all remaining market stock to storage, creates `RETURN_FROM_MARKET` transactions, publishes `MarketClosedEvent` |
| `UpdateMarketUseCase` | Updates market metadata (only allowed in `SCHEDULED`) |
| `DeleteMarketUseCase` | Deletes market (not allowed when `OPEN`) |
| `MarketQueryUseCase` | Reads market(s); computes per-market and aggregate summaries (allocated, current, sold, revenue) |

**Market Stock:**

| Use Case | What it does |
|---|---|
| `ShiftItemToMarketUseCase` | Deducts from storage stock, creates/updates `MarketItem`, creates `SHIFT_TO_MARKET` transaction, publishes `ItemShiftedToMarketEvent` |
| `IncrementMarketStockUseCase` | Adds stock to market item, creates `INCREMENT` transaction |
| `DecrementMarketStockUseCase` | Removes stock from market item (records sale), creates `SALE` transaction with optional price data |
| `SetMarketItemPriceUseCase` | Sets/updates market price on a `MarketItem` |
| `MarketItemQueryUseCase` | Reads market items |

**Other:**

| Use Case | What it does |
|---|---|
| `CategoryUseCase` | CRUD for categories scoped to user |
| `StorageSummaryUseCase` | Returns all items with their current storage stock levels |
| `TransactionQueryUseCase` | Filters transactions by userId, marketId, or itemId |

---

### 3. Infrastructure Layer (`ims-infrastructure`)

Provides concrete implementations of all domain ports.

#### JPA Entities and Tables

| Entity | Table | Notable Columns |
|---|---|---|
| `ItemJpaEntity` | `items` | user_id, sku, storage_zone/shelf/row/column, total_storage_stock |
| `MarketJpaEntity` | `markets` | user_id, status (SCHEDULED/OPEN/CLOSED) |
| `MarketItemJpaEntity` | `market_items` | market_id, item_id (unique pair), allocated_stock, current_stock |
| `CategoryJpaEntity` | `categories` | user_id, name |
| `TransactionJpaEntity` | `transactions` | user_id, market_id, item_id, type, quantity_delta, stock_before, stock_after, sale_price, sale_currency |
| `UserJpaEntity` | `users` | email (unique), password (BCrypt), role |
| `OutboxEventJpaEntity` | `outbox_events` | event_type, aggregate_id, payload (JSON), status (PENDING/PUBLISHED/FAILED) |

#### Repository Adapters

Each adapter implements the corresponding domain port, translates between JPA entities and domain models, and delegates to a Spring Data JPA repository.

```
ItemRepositoryAdapter      → ItemJpaRepository      (implements ItemRepositoryPort)
MarketRepositoryAdapter    → MarketJpaRepository     (implements MarketRepositoryPort)
MarketItemRepositoryAdapter→ MarketItemJpaRepository (implements MarketItemRepositoryPort)
CategoryRepositoryAdapter  → CategoryJpaRepository   (implements CategoryRepositoryPort)
TransactionRepositoryAdapter→TransactionJpaRepository(implements TransactionRepositoryPort)
UserRepositoryAdapter      → UserJpaRepository       (implements UserRepositoryPort)
OutboxEventPublisherAdapter→ OutboxEventJpaRepository(implements DomainEventPublisherPort)
```

#### Outbox Pattern

Domain events are never published directly to Kafka. Instead:

1. `OutboxEventPublisherAdapter.publish(event)` serialises the event to JSON and inserts a row into `outbox_events` with status `PENDING` — inside the same database transaction as the business operation.
2. `OutboxPublisherJob` (a `@Scheduled` task, default poll interval 500 ms) reads `PENDING` rows and publishes them to the relevant Kafka topic.
3. On success the row is marked `PUBLISHED`; on failure it is marked `FAILED`.

This guarantees events are never lost even if Kafka is temporarily unavailable at the time of the business transaction.

#### Kafka Topics

| Topic | Events |
|---|---|
| `ims.items` | ItemCreatedEvent, ItemStockAdjustedEvent, ItemShiftedToMarketEvent |
| `ims.markets` | MarketOpenedEvent, MarketClosedEvent |
| `ims.market-stock` | MarketStockIncrementedEvent, MarketStockDecrementedEvent |

#### AOP

| Aspect | Annotation | Behaviour |
|---|---|---|
| `LoggingAspect` | (pointcut-based) | Logs method entry and exit at DEBUG level |
| `PerformanceAspect` | `@Monitored` | Measures and logs execution time |

#### Database Migrations (Flyway)

Migrations run on startup, in order:

| Version | Description |
|---|---|
| V1 | Create `items` table |
| V2 | Create `markets` table |
| V3 | Create `market_items` table with unique (market_id, item_id) |
| V4 | Create `transactions` table with indexes on market_id, item_id, occurred_at |
| V5 | Create `outbox_events` table |
| V6 | Fix currency column types |
| V7 | Create `categories` table |
| V8 | Create `users` table |
| V9 | Add `sale_price`, `sale_currency` to transactions |
| V10 | Convert market dates from DATE to TIMESTAMP |
| V11 | Add `user_id` (UUID FK → users) to items, markets, categories, transactions; change SKU uniqueness to (user_id, sku); add indexes |

---

### 4. API Layer (`ims-api`)

Handles HTTP concerns: routing, validation, serialisation, and authentication.

#### Security

**JWT Authentication:**
- Algorithm: HMAC-SHA256
- Token lifetime: 24 hours (configurable via `JWT_EXPIRATION_MS`)
- Claim subject: user email
- Configurable secret via `JWT_SECRET` env var

**Filter chain:**
1. `JwtAuthFilter` (extends `OncePerRequestFilter`) extracts the `Authorization: Bearer <token>` header, validates the token, loads `UserDetails`, and sets the `SecurityContext`.
2. Spring Security enforces access rules per route.

**Public endpoints** (no token required):
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `GET /api/v1/hello`
- `/swagger-ui/**`, `/api-docs/**`, `/actuator/**`

All other endpoints require a valid JWT.

**`CurrentUserService`:**
Every authenticated controller endpoint calls `currentUserService.getCurrentUserId()` which resolves the authenticated user's UUID from `SecurityContextHolder`. This UUID is then passed as the first argument to every command and query, ensuring each user can only access their own data.

#### REST Endpoints

**Auth** — `POST /api/v1/auth`

| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/login` | Authenticate, returns JWT | No |
| POST | `/register` | Create account (requires registration code) | No |

**Items** — `/api/v1/items`

| Method | Path | Description |
|---|---|---|
| POST | `/` | Register a new item |
| GET | `/` | List items (optional: `?category=`) |
| GET | `/{id}` | Get item by ID |
| PUT | `/{id}` | Update item |
| PATCH | `/{id}/stock` | Adjust storage stock (delta) |
| DELETE | `/{id}` | Delete item |
| GET | `/{id}/transactions` | Item's transaction history |

**Markets** — `/api/v1/markets`

| Method | Path | Description |
|---|---|---|
| POST | `/` | Create market (starts as SCHEDULED) |
| GET | `/` | List markets (optional: `?status=`) |
| GET | `/{id}` | Get market |
| PUT | `/{id}` | Update market |
| DELETE | `/{id}` | Delete market |
| POST | `/{id}/open` | Open market (SCHEDULED → OPEN) |
| POST | `/{id}/close` | Close market (OPEN → CLOSED), returns stock |
| GET | `/{id}/summary` | Per-market analytics |
| GET | `/summary` | Cross-market analytics (optional: `?status=`) |

**Market Stock** — `/api/v1/markets`

| Method | Path | Description |
|---|---|---|
| POST | `/{id}/items` | Shift item from storage to market |
| GET | `/{id}/items` | All items in market |
| GET | `/{id}/items/{itemId}` | Single market item |
| PATCH | `/{id}/items/{itemId}/increment` | Add market stock |
| PATCH | `/{id}/items/{itemId}/decrement` | Remove market stock (records sale) |
| PUT | `/{id}/items/{itemId}/price` | Set item price in market |
| GET | `/{id}/items/{itemId}/transactions` | Market-item transaction history |

**Categories** — `/api/v1/categories`

| Method | Path | Description |
|---|---|---|
| GET | `/` | List categories |
| POST | `/` | Create category |
| DELETE | `/{id}` | Delete category |

**Storage** — `/api/v1/storage`

| Method | Path | Description |
|---|---|---|
| GET | `/summary` | Storage summary — all items with current stock |

**Transactions** — `/api/v1/transactions`

| Method | Path | Description |
|---|---|---|
| GET | `/` | All transactions (optional: `?marketId=&itemId=`) |

#### API DTOs

**Requests:**

| DTO | Key Fields |
|---|---|
| `LoginRequest` | email, password |
| `RegisterRequest` | email, password, registrationCode (6 chars) |
| `RegisterItemRequest` | sku, name, description, category, defaultPrice, currency, zone, shelf, row, column, initialStock |
| `UpdateItemRequest` | name, description, category, defaultPrice, currency, zone, shelf, row, column |
| `AdjustStockRequest` | delta, note, createdBy |
| `CreateMarketRequest` | name, place, openDate, closeDate |
| `UpdateMarketRequest` | name, place, openDate, closeDate |
| `ShiftItemRequest` | itemId, quantity, marketPrice, currency, createdBy |
| `IncrementStockRequest` | quantity, note, createdBy |
| `DecrementStockRequest` | quantity, note, createdBy, salePrice, saleCurrency |
| `SetPriceRequest` | price, currency |
| `CreateCategoryRequest` | name |

**Responses:**

| DTO | Key Fields |
|---|---|
| `AuthResponse` | token, expirationMs, email |
| `ItemResponse` | id, sku, name, description, category, defaultPrice, currency, zone, shelf, row, column, totalStorageStock, createdAt, updatedAt |
| `MarketResponse` | id, name, place, openDate, closeDate, status, createdAt |
| `MarketItemResponse` | id, marketId, itemId, allocatedStock, currentStock, marketPrice, currency |
| `MarketSummaryResponse` | marketId, marketName, totalItemTypes, totalAllocatedStock, totalCurrentStock, totalSold, totalRevenue, currency, items |
| `AllMarketsSummaryResponse` | totalMarkets, totalItemsSold, totalRevenue, currency, markets |
| `CategoryResponse` | id, name, createdAt |
| `TransactionResponse` | id, marketId, itemId, type, quantityDelta, stockBefore, stockAfter, note, occurredAt, createdBy, salePrice, saleCurrency |
| `StorageSummaryResponse` | items (list of StorageItemResponse) |
| `StorageItemResponse` | itemId, sku, name, category, currentStock |

#### Error Handling

`GlobalExceptionHandler` maps domain exceptions to HTTP status codes:

| Exception | HTTP Status |
|---|---|
| `ItemNotFoundException`, `MarketNotFoundException`, `MarketItemNotFoundException` | 404 Not Found |
| `DuplicateSkuException` | 409 Conflict |
| `InsufficientStockException` | 422 Unprocessable Entity |
| `InvalidMarketStateException` | 409 Conflict |
| `IllegalArgumentException` | 409 Conflict |
| `IllegalStateException` | 500 Internal Server Error |
| Validation errors (`MethodArgumentNotValidException`) | 400 Bad Request |

---

## Key Business Flows

### Item Registration

```
POST /api/v1/items
    → ItemController.registerItem()
    → currentUserService.getCurrentUserId()
    → RegisterItemUseCase.registerItem(RegisterItemCommand)
        → itemRepository.findBySkuAndUserId() — check no duplicate
        → Item.create(userId, sku, ...) — build domain model
        → itemRepository.save(item)
        → if initialStock > 0:
            item.adjustStock(initialStock)
            transactionRepository.save(STOCK_ADJUSTMENT transaction)
        → eventPublisher.publish(ItemCreatedEvent)  — writes to outbox_events
    ← ItemResponse
```

### Shift Item to Market

```
PATCH /api/v1/markets/{id}/items
    → MarketStockController.shiftItem()
    → currentUserService.getCurrentUserId()
    → ShiftItemToMarketUseCase.shiftItem(ShiftItemCommand)
        → marketRepository.findById() — verify market exists
        → check market.isOpen() || market.isScheduled()
        → itemRepository.findById() — verify item exists
        → check item.getTotalStorageStock() >= quantity
        → item.adjustStock(-quantity) — deduct from storage
        → itemRepository.save(item)
        → marketItemRepository.findByMarketIdAndItemId() — find or create
        → marketItem.addStock(quantity)
        → marketItemRepository.save(marketItem)
        → transactionRepository.save(SHIFT_TO_MARKET transaction)
        → eventPublisher.publish(ItemShiftedToMarketEvent)
    ← MarketItemResponse
```

### Close Market

```
POST /api/v1/markets/{id}/close
    → MarketController.closeMarket()
    → currentUserService.getCurrentUserId()
    → CloseMarketUseCase.closeMarket(CloseMarketCommand)
        → marketRepository.findById() — verify market exists
        → market.close() — validates OPEN state, sets CLOSED
        → for each MarketItem in market:
            remaining = marketItem.getCurrentStock()
            item.adjustStock(+remaining) — return to storage
            itemRepository.save(item)
            transactionRepository.save(RETURN_FROM_MARKET transaction)
        → marketRepository.save(market)
        → eventPublisher.publish(MarketClosedEvent)
    ← MarketResponse
```

### Record a Sale

```
PATCH /api/v1/markets/{id}/items/{itemId}/decrement
    → MarketStockController.decrementStock()
    → currentUserService.getCurrentUserId()
    → DecrementMarketStockUseCase.decrementStock(DecrementStockCommand)
        → marketItemRepository.findByMarketIdAndItemId()
        → marketItem.decrement(quantity)  — throws if insufficient stock
        → marketItemRepository.save(marketItem)
        → transactionRepository.save(SALE transaction with salePrice/saleCurrency)
        → eventPublisher.publish(MarketStockDecrementedEvent)
    ← MarketItemResponse
```

---

## Multi-Tenancy (Per-User Data Isolation)

Every entity that belongs to a user (`items`, `markets`, `categories`, `transactions`) has a `user_id` UUID column with a foreign key to `users(id)`.

The isolation is enforced at the application layer:

1. `CurrentUserService` resolves the authenticated user's UUID from `SecurityContextHolder` by looking up the JWT's email claim in the `users` table.
2. Every controller endpoint calls `currentUserService.getCurrentUserId()` and passes the resulting `userId` as the first argument of every command and query object.
3. Every repository port method is scoped to `userId` (e.g. `findAllByUserId(UUID)`, `findBySkuAndUserId(String, UUID)`).
4. No query ever returns data from another user.

---

## Configuration Reference

All configuration lives in `ims-bootstrap/src/main/resources/application.yml`. Sensitive values are injected via environment variables.

| Property | Env Var | Default | Description |
|---|---|---|---|
| `server.port` | `PORT` | `8080` | HTTP listen port |
| `spring.datasource.url` | `DB_HOST`, `DB_PORT`, `DB_NAME` | `localhost:5432/imsdb` | PostgreSQL connection |
| `spring.datasource.username` | `DB_USERNAME` | `ims` | DB username |
| `spring.datasource.password` | `DB_PASSWORD` | `ims` | DB password |
| `spring.kafka.bootstrap-servers` | `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka brokers |
| `jwt.secret` | `JWT_SECRET` | (dev key) | HMAC-SHA256 signing key |
| `jwt.expiration-ms` | `JWT_EXPIRATION_MS` | `86400000` | Token lifetime (24 h) |
| `registration.code` | `REGISTRATION_CODE` | `IMS001` | Required code to create an account |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Frontend origin(s) |
| `ims.outbox.poll-ms` | `OUTBOX_POLL_MS` | `500` | Outbox polling interval |

---

## Technology Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Build | Maven (multi-module) |
| Persistence | PostgreSQL 16, Spring Data JPA, Hibernate |
| Migrations | Flyway |
| Authentication | JWT (JJWT 0.12.6), Spring Security 6 |
| Messaging | Apache Kafka |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI at `/swagger-ui.html`) |
| Observability | Spring Boot Actuator, Logstash Logback, AOP logging |
| Containerisation | Docker, Docker Compose |
