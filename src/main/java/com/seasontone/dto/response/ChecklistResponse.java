package com.seasontone.dto.response;

import com.seasontone.dto.ChecklistItemDto;
import java.time.Instant;

public record ChecklistResponse(
    Long checkId,
    Long userId,
    Long listingId,
    String title,
    String notes,
    Instant createdAt,
    Instant updatedAt,
    ChecklistItemDto items
) {}
