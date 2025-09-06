package com.seasontone.dto.checklist;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChecklistGroupResponse {
	private String address;

	// 가장 최근 데이터 요약
	private String latestName;
	private int latestMonthly;
	private int latestDeposit;
	private int latestMaintenanceFee;
	private double latestScore;

	private List<ChecklistDetailsResponse> checklists;

	@Getter
	@Builder
	public static class ChecklistDetailsResponse {
		private Long id;
		private String name;
		private int monthly;
		private int deposit;
		private int maintenanceFee;
		private double score;
	}
}

