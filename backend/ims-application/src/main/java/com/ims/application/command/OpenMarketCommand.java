package com.ims.application.command;

import java.util.UUID;

public record OpenMarketCommand(UUID userId, UUID marketId) {}
