package com.ims.application.command;

import java.util.UUID;

public record CloseMarketCommand(UUID marketId, String createdBy) {}
