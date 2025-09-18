package com.seasontone.service.listing;

import com.seasontone.domain.listing.Listing;
import com.seasontone.dto.listing.KakaoAddressResponse;
import com.seasontone.dto.response.MapMarkerResponse;
import com.seasontone.repository.listing.ListingRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ListingService {
	private static final String API_URL = "https://dapi.kakao.com/v2/local/search/address.json";
	@Value("${openai.api-key:${OPENAI_API_KEY:}}")
	private String REST_API_KEY;
	private final String API_KEY = "KakaoAK "; // 실제 키로 교체

	@PostConstruct
	void verifyKeys() {
		if (REST_API_KEY == null || REST_API_KEY.isBlank()) {
			throw new IllegalStateException("Kakao key missing. Set `kakao.rest-api.key` or env `KAKAO_REST_API_KEY`");
		}
	}

	private final RestTemplate restTemplate = new RestTemplate();

	private final ListingRepository listingRepository;

	public Listing createListing(String roadAddress, String listingName) {
		KakaoAddressResponse.Document coordinates = getCoordinates(roadAddress);
		Listing listing = Listing.builder()
				.listingName(listingName)
				.latitude(new BigDecimal(coordinates.getY()))
				.longitude(new BigDecimal(coordinates.getX()))
				.roadAddress(roadAddress)
				.build();

		listingRepository.save(listing);
		return listing;
	}



	public KakaoAddressResponse.Document getCoordinates(String query) {
		// Header 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", API_KEY + REST_API_KEY);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		// 요청 실행 (DTO 매핑)
		ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(
				API_URL + "?query={query}",
				HttpMethod.GET,
				request,
				KakaoAddressResponse.class,
				query
		);

		KakaoAddressResponse body = response.getBody();

		return Optional.ofNullable(body)
				.filter(b -> !b.getDocuments().isEmpty())
				.map(b -> b.getDocuments().get(0))
				.orElseThrow(() -> new IllegalArgumentException("좌표 변환 실패: " + query));
	}

	@Transactional(readOnly = true)
	public List<MapMarkerResponse> getMarkersDedupByRoadAddress() {
		return listingRepository.findMapMarkersRaw().stream()
				.map(row -> new MapMarkerResponse(
						((Number) row[0]).longValue(),      // listingId
						(String) row[1],                    // roadAddress
						(BigDecimal) row[2],                // latitude
						(BigDecimal) row[3],                // longitude
						(String) row[4],                    // listingName
						((Number) row[5]).longValue()       // checklistCount
				))
				.toList();
	}

	//도로명으로 조회 도움
	@Transactional(readOnly = true)
	public String getRoadAddressOf(Long listingId) {
		return listingRepository.findById(listingId)
				.orElseThrow(() -> new EntityNotFoundException("Listing not found: " + listingId))
				.getRoadAddress();
	}
}
