package com.ims.application.command;

import java.util.UUID;

public record CloseMarketCommand(UUID userId, UUID marketId, String createdBy) {}
