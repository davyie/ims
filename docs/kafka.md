# Kafka Integration

## Overview

Kafka is used as an event bus for broadcasting state changes to external systems. Events are published using the **Transactional Outbox Pattern** to guarantee delivery even when Kafka is temporarily unavailable.

No consumers exist within this codebase — the topics are intended for external systems (analytics, notifications, service sync, etc.).

---

## Topics and Events

| Event | Topic | Triggered by |
|---|---|---|
| `ItemCreated` | `ims.items` | Register item |
| `ItemStockAdjusted` | `ims.items` | Adjust storage stock |
| `MarketOpened` | `ims.markets` | Open market |
| `MarketClosed` | `ims.markets` | Close market |
| `ItemShiftedToMarket` | `ims.market-stock` | Shift item to market |
| `MarketStockIncremented` | `ims.market-stock` | Increment market stock |
| `MarketStockDecremented` | `ims.market-stock` | Decrement market stock / record sale |

Each topic is configured with 1 partition and 1 replica.

---

## Transactional Outbox Pattern

Rather than publishing directly to Kafka inside a use case, events are written to an `outbox_events` database table **in the same transaction** as the business data. A separate scheduled job then polls and forwards them to Kafka.

```
Use Case (e.g. ShiftItemToMarketUseCase)
  → saves MarketItem to DB
  → calls eventPublisher.publish(ItemShiftedToMarketEvent)
      → OutboxEventPublisherAdapter writes row to outbox_events (status=PENDING)
  → transaction commits  ← both writes are atomic

OutboxPublisherJob (runs every 500ms)
  → finds all PENDING rows
  → sends JSON payload to Kafka topic (aggregate ID as message key)
  → marks row PUBLISHED
  → on failure → marks FAILED, logs error
```

This guarantees that if the business operation succeeds, the event will eventually reach Kafka.

### Outbox table schema

```sql
CREATE TABLE outbox_events (
    id           UUID PRIMARY KEY,
    event_type   VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload      TEXT NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    occurred_at  TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);
```

`status` values: `PENDING` → `PUBLISHED` or `FAILED`.

---

## Architecture

The domain and application layers are fully decoupled from Kafka through the `DomainEventPublisherPort` interface:

```
Domain / Application layer
  DomainEventPublisherPort  (interface — no Kafka dependency)

Infrastructure layer
  OutboxEventPublisherAdapter  implements DomainEventPublisherPort
    → writes to outbox_events table

  OutboxPublisherJob  (@Scheduled)
    → polls outbox_events for PENDING rows
    → sends via KafkaTemplate<String, String>
    → updates status
```

### Key files

| File | Purpose |
|---|---|
| `ims-domain/.../port/DomainEventPublisherPort.java` | Application layer contract |
| `ims-domain/.../event/` | Domain event classes |
| `ims-infrastructure/.../outbox/OutboxEventPublisherAdapter.java` | Writes events to outbox table |
| `ims-infrastructure/.../kafka/OutboxPublisherJob.java` | Polls outbox and publishes to Kafka |
| `ims-infrastructure/.../kafka/KafkaConfig.java` | Topic bean definitions |
| `ims-infrastructure/.../jpa/entity/OutboxEventJpaEntity.java` | Outbox table JPA mapping |
| `V5__create_outbox_events.sql` | Database migration for outbox table |

---

## Configuration

| Property | Default | Description |
|---|---|---|
| `KAFKA_BOOTSTRAP` | `localhost:9092` | Kafka broker address |
| `OUTBOX_POLL_MS` | `500` | Outbox polling interval in milliseconds |

Producer settings: `acks=all`, `retries=3`, `StringSerializer` for key and value.

The **test profile** sets `OUTBOX_POLL_MS` to 3,600,000 ms (1 hour) to suppress connection errors when Kafka is not running during tests.

---

## Reliability Guarantees

- **Atomicity** — business data and the outbox event are written in the same DB transaction; if the operation fails, no event is stored.
- **At-least-once delivery** — the polling job will retry until the event is marked `PUBLISHED`.
- **Ordering** — the aggregate ID (item ID or market ID) is used as the Kafka message key, ensuring events for the same aggregate arrive in order.
- **Kafka durability** — `acks=all` ensures the broker acknowledges writes to all replicas before confirming.
