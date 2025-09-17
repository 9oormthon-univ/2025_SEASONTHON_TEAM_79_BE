package com.seasontone.dto.checklists;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seasontone.domain.listing.Listing;
import jakarta.validation.constraints.Size;

public record ChecklistItemDto(
    @Size(max = 255) String name,
    @Size(max = 255) String address,
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
