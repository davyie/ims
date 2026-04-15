package com.ims.transaction.adapter.out.persistence;

import com.ims.transaction.domain.model.TransactionRecord;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public final class TransactionRecordSpec {

    private TransactionRecordSpec() {}

    public static Specification<TransactionRecord> withFilters(
            UUID userId,
            String originService,
            String eventType,
            Instant from,
            Instant to) {

        return Specification
                .where(userIdEquals(userId))
                .and(originServiceEquals(originService))
                .and(eventTypeEquals(eventType))
                .and(recordedAtAfter(from))
                .and(recordedAtBefore(to));
    }

    private static Specification<TransactionRecord> userIdEquals(UUID userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    private static Specification<TransactionRecord> originServiceEquals(String originService) {
        return (root, query, cb) ->
                originService == null ? null : cb.equal(root.get("originService"), originService);
    }

    private static Specification<TransactionRecord> eventTypeEquals(String eventType) {
        return (root, query, cb) ->
                eventType == null ? null : cb.equal(root.get("eventType"), eventType);
    }

    private static Specification<TransactionRecord> recordedAtAfter(Instant from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("recordedAt"), from);
    }

    private static Specification<TransactionRecord> recordedAtBefore(Instant to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("recordedAt"), to);
    }
}
