package com.seasontone.dto.response;

import java.math.BigDecimal;

public record MapMarkerResponse(
    Long listingId,             // 대표로 선택된 listing의 id
    String roadAddress,         // 도로명주소 (그룹 키)
    BigDecimal latitude,        // 위도
    BigDecimal longitude,       // 경도
    String listingName,         // 대표 listing의 이름(있으면)
    long checklistCount         // 해당 도로명주소에 속한 체크리스트 총 개수
) {}
