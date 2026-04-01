package com.ims.application.query;

import com.ims.domain.model.MarketStatus;
import java.util.UUID;

public record ListMarketsQuery(UUID userId, MarketStatus status) {}
