package com.seasontone.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyChecklistResponse {
	private Long id; //id
	private String aptNm; // 아파트명
	private String address; // 주소
	private int deposit; // 보증금
	private int monthly; // 월세
	private int maintenanceFee; // 관리비
	private int floorAreaSqm;
	private double avgScore; //평점
}
