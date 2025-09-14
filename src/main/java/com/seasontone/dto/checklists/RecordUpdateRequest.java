package com.seasontone.dto.checklists;

public record RecordUpdateRequest(
    String name,
    String address,
    Boolean monthly,
	Integer deposit,
	Integer  maintenanceFee,
	Integer floorAreaSqm,
    Integer mining, //채광
    Integer water,
    Integer cleanliness,
    Integer options,
    Integer security,
    Integer noise,
    Integer surroundings, //주변환경
    Integer recycling, //분리수거
    Boolean isElevatorAvailable,
    Boolean isBalconyAvailable,
    Boolean isPetAllowed,
    String notes
) {}
