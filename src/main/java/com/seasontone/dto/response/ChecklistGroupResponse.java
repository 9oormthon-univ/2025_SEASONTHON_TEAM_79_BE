package com.seasontone.dto.response;

import com.seasontone.dto.photo.PhotoDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistGroupResponse {
	private String address;
	// 가장 최근 데이터 요약
	private String latestName;
	private int latestMonthly;
	private int latestDeposit;
	private int latestMaintenanceFee;
	private int latestFloorAreaSqm;
	private double avgScore;

	private List<ChecklistDetailsResponse> checklists;

	@Getter
	@Builder
	public static class ChecklistDetailsResponse {
		private Long id;
		private String name;
		private String description;
		private int area;
		private int monthly;
		private int deposit;
		private int maintenanceFee;
		private int floorAreaSqm;
		private double score;
		private List<PhotoDto> photos;
	}
}

