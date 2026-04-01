package com.ims.application.query;

import com.ims.domain.model.MarketStatus;
import java.util.UUID;

public record GetAllMarketsSummaryQuery(UUID userId, MarketStatus status) {}
