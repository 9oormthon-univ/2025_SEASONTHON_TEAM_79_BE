package com.seasontone.dto;

import jakarta.validation.Valid;

//전체 업데이트
public record ChecklistUpdateRequest(
    Long listingId,
    String title,
    String notes,
    @Valid ChecklistItemDto items
) {}