package com.seasontone.dto.response;

import com.seasontone.domain.enums.RentType;
import com.seasontone.domain.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistPreviewResponse {
  private String address;
  private String detailAddress;
  private RentType rentType;
  private RoomType roomType;
  private String latestName;
  private int area;
  private int latestMonthly;
  private int latestDeposit;
  private int latestMaintenanceFee;
  private int latestFloorAreaSqm;
  private double avgScore; // 소수 1자리로 라운딩하여 세팅
}
