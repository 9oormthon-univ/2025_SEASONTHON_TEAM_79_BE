package com.seasontone.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapChecklistItemResponse {
  private Long id;
  private String name;
  private String address;
  private String detailAddress;
  private Integer area;

  private Integer monthly;
  private Integer deposit;
  private Integer maintenanceFee;
  private Integer floorAreaSqm;

  private Double score;
  //private List<PhotoDto> photos;
}
