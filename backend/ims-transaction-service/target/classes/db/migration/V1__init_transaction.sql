CREATE TABLE IF NOT EXISTS transaction_records (
    record_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id        UUID NOT NULL UNIQUE,
    correlation_id  UUID,
    event_type      VARCHAR(100) NOT NULL,
    origin_service  VARCHAR(100) NOT NULL,
    entity_id       UUID,
    user_id         UUID,
    occurred_at     TIMESTAMP WITH TIME ZONE,
    recorded_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    payload         JSONB,
    kafka_topic     VARCHAR(200),
    kafka_partition INTEGER
);

-- Append-only: no UPDATE or DELETE permissions should be granted on this table.

CREATE INDEX idx_tx_event_id       ON transaction_records(event_id);
CREATE INDEX idx_tx_correlation_id ON transaction_records(correlation_id);
CREATE INDEX idx_tx_user_id        ON transaction_records(user_id);
CREATE INDEX idx_tx_origin_service ON transaction_records(origin_service);
CREATE INDEX idx_tx_event_type     ON transaction_records(event_type);
CREATE INDEX idx_tx_recorded_at    ON transaction_records(recorded_at DESC);
