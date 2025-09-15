package com.seasontone.dto.checklists;

import jakarta.validation.Valid;

//전체 업데이트
public record ChecklistUpdateRequest(
    @Valid ChecklistItemDto items
){}