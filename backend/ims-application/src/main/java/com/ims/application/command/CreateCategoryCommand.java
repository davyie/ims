package com.ims.application.command;

import java.util.UUID;

public record CreateCategoryCommand(UUID userId, String name) {}
