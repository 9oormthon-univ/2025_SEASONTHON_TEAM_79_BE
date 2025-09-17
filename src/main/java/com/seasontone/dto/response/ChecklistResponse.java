package com.seasontone.dto.response;

import com.seasontone.dto.checklists.ChecklistItemDto;
import com.seasontone.dto.photo.PhotoDto;
import java.util.List;

public record ChecklistResponse(
    Long checkId,
    Long userId,
    Double avgScore,
    ChecklistItemDto items,
    List<PhotoDto> photos
) {}