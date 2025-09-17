package com.seasontone.dto.checklists;

import jakarta.validation.constraints.NotNull;

public record ChecklistCreateRequest(
    @NotNull Long userId,
    ChecklistItemDto items
){}