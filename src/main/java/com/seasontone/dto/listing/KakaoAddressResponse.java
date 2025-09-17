package com.seasontone.dto.listing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class KakaoAddressResponse {
	private List<Document> documents;

	@Data
	public static class Document {
		private String x;  // 경도
		private String y;  // 위도
		@JsonProperty("address_name")
		private String roadAddress ;
	}
}
