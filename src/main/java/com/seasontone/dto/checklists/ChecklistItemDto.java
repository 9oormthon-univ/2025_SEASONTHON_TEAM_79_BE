package com.seasontone.dto.checklists;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seasontone.domain.enums.RentType;
import com.seasontone.domain.enums.RoomType;
import com.seasontone.domain.listing.Listing;
import jakarta.validation.constraints.Size;

public record ChecklistItemDto(
		@Size(max = 255) String name,
		@Size(max = 255) String address,
		String detailAddress,        // 상세주소
		RentType rentType,           // 전세/월세
		RoomType roomType,            // 원룸/투룸/3룸+
		Integer area,
		Integer monthly,
		Integer deposit,
		Integer  maintenanceFee,
		Integer floorAreaSqm,
		Integer mining,
		Integer water,
		Integer cleanliness,
		Integer options,
		Integer security,
		Integer noise,
		Integer surroundings,
		Integer recycling,
		Boolean elevator,
		Boolean veranda,
		Boolean pet,
		String memo,

		Listing listing,
		@JsonProperty("voicenote")
		String voiceNote
) {}
