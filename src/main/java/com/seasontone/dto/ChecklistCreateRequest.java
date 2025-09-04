package com.seasontone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChecklistCreateRequest(
    @NotNull Long userId,
    Long listingId,
    @Size(max = 200) String title,
    String notes,
    ChecklistItemDto items
) {}