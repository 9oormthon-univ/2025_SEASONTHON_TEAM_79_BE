package com.seasontone.dto.response;

import java.time.Instant;

public record RecordSummaryResponse(
    Long id,
    Double avgScore,
    String aptNm,
    String address,
    Integer deposit,
    Integer monthlyRent,
    Integer maintenanceFee,
    Integer maxScore,
    Integer secondMaxScore,
    Integer minScore,
    Instant visitedAt
) {}