package com.seasontone.dto.photo;

import java.time.Instant;

public record PhotoDto(
    Long id,
    String filename,
    String contentType,
    Long size,
    String caption,
    Instant createdAt,
    String rawUrl // /api/checklists/{checkId}/photos/{photoId}/raw
) {}
