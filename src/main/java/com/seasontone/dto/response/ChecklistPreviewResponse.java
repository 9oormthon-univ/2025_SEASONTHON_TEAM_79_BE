package com.seasontone.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistPreviewResponse {
  private String address;
  private String latestName;
  private int latestArea;
  private int latestMonthly;
  private int latestDeposit;
  private int latestMaintenanceFee;
  private int latestFloorAreaSqm;
  private double avgScore; // 소수 1자리로 라운딩하여 세팅
}
