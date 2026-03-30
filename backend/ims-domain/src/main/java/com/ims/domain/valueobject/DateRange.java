package com.ims.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public record DateRange(LocalDate from, LocalDate to) {
    public DateRange {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("DateRange 'from' must not be after 'to'");
        }
    }
}
