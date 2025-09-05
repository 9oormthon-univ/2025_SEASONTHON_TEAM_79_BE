package com.seasontone.dto;

import java.time.Instant;

public record VoiceNoteDto(
    String filename,
    String contentType,
    Long size,
    Integer durationSec,
    String transcript,   // 나중에 Whisper 붙으면 채워짐
    String summary,      // 나중에 요약 붙으면 채워짐
    Instant createdAt,
    Instant updatedAt,
    String rawUrl        // /api/checklists/{checkId}/audio/raw
) {}
