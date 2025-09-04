package com.seasontone.dto;

import jakarta.validation.constraints.Size;

public record ChecklistItemDto(
    @Size(max = 255) String name,
    @Size(max = 255) String address,
    Boolean monthly,
    Integer mining,
    Integer water,
    Integer cleanliness,
    Integer options,
    Integer security,
    Integer noise,
    Integer surroundings,
    Integer recycling,
    Boolean elevator,
    Boolean veranda,
    Boolean pet,
    String memo
) {}