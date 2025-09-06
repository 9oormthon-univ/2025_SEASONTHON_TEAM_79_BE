package com.seasontone.dto.response;

import com.seasontone.dto.ChecklistItemDto;
import com.seasontone.dto.PhotoDto;
import com.seasontone.dto.VoiceNoteDto;
import java.time.Instant;
import java.util.List;

public record ChecklistResponse(
    Long checkId,
    Long userId,
    Long listingId,
    String title,
    String notes,
    Instant createdAt,
    Instant updatedAt,
    Double avgScore,
    ChecklistItemDto items,
    List<PhotoDto> photos,     // 메타만
    VoiceNoteDto voiceNote     // 메타만
) {}
