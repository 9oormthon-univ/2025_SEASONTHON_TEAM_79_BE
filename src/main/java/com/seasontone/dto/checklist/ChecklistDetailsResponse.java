package com.seasontone.dto.checklist;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChecklistDetailsResponse {
	private Long id;
	private House house;
	private Checklist checklist;
	private String memo;

	@Getter
	@Setter
	@Builder
	public static class House {
		private String address;
		private String name;
		private int monthly;
		private int deposit;
		private int  maintenanceFee;
		private double scores;
	}

	@Getter
	@Setter
	@Builder
	public static class Checklist {
		private int mining;
		private int water;
		private int cleanliness;
		private int options;
		private int security;
		private int noise;
		private int surroundings;
		private int recycling;
		private boolean elevator;
		private boolean veranda;
		private boolean pet;
	}
}
