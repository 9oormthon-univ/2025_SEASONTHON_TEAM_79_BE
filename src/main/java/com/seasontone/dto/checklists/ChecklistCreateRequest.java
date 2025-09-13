package com.seasontone.dto.checklists;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChecklistCreateRequest(
    @NotNull Long userId,
    Long listingId,
    @Size(max = 200) String title,
    String notes,
    ChecklistItemDto items
) {}