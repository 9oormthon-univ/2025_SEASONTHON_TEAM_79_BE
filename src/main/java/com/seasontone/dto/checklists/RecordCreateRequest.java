package com.seasontone.dto.checklists;

public record RecordCreateRequest(
    // 매물 기본
    String aptNm,
    String address,
    Integer deposit,
    Integer monthlyRent,
    Integer maintenanceFee,
	Integer floorAreaSqm,

    // 사진/음성은 추후 multipart로 분리. 지금은 생략/널 허용
    // char 타입은 사용하지 않음

    // 점수/옵션
    Integer mining,
    Integer water,
    Integer cleanliness,
    Integer options,
    Integer security,
    Integer noise,
    Integer surroundings,
    Integer recycling,
    Boolean isElevatorAvailable,
    Boolean isBalconyAvailable,
    Boolean isPetAllowed,

    String notes
) {}
