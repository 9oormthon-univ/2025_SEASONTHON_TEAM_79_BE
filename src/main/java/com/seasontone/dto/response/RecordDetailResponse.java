package com.seasontone.dto.response;

public record RecordDetailResponse(
    Long id,
    Double avgScore,
    String name,
    String address,
    Integer deposit,
    Integer monthlyRent,
    Integer maintenanceFee,
    Integer mining,
    Integer water,
    Integer cleanliness,
    Integer options,
    Integer security,
    Integer noise,
    Integer surroundings,
    Integer elevator,
    Boolean isElevatorAvailable,
    Boolean isPetAllowed,
    String notes
) {}