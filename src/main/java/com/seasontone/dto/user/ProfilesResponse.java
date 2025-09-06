package com.seasontone.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfilesResponse {
	private Long userId;
	private String name;
	private String region;
}
