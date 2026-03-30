package com.ims.domain.valueobject;

import java.util.Objects;

public record StoragePosition(String zone, String shelf, int row, int column) {
    public StoragePosition {
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(shelf, "shelf must not be null");
        if (zone.isBlank()) throw new IllegalArgumentException("zone must not be blank");
        if (!zone.matches("[A-Za-z0-9]+")) throw new IllegalArgumentException("zone must be alphanumeric");
        if (shelf.isBlank()) throw new IllegalArgumentException("shelf must not be blank");
        if (row < 0) throw new IllegalArgumentException("row must be >= 0");
        if (column < 0) throw new IllegalArgumentException("column must be >= 0");
    }
}
