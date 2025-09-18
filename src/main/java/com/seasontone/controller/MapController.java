package com.seasontone.controller;

import com.seasontone.dto.response.MapChecklistItemResponse;
import com.seasontone.dto.response.MapMarkerResponse;
import com.seasontone.service.checklist.ChecklistService;
import com.seasontone.service.listing.ListingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapController {

  private final ListingService listingService;
  private final ChecklistService checklistService;

  // GET /api/map/markers
  @GetMapping("/markers")
  public List<MapMarkerResponse> getMarkers() {
    return listingService.getMarkersDedupByRoadAddress();
  }

  // GET /api/map/{listingId}/checklists
  @GetMapping("/{listingId}/checklists")
  public List<MapChecklistItemResponse> getChecklists(@PathVariable Long listingId) {
    return checklistService.listAllByMarker(listingId);
  }

  /*
  //지도 커지면 값 전부 반환하기 힘듦 지도 뷰포트로 서버측 필터링
  @GetMapping("/markers")
public List<MapMarkerResponse> getMarkers(
    @RequestParam(required = false) Double swLat,
    @RequestParam(required = false) Double swLng,
    @RequestParam(required = false) Double neLat,
    @RequestParam(required = false) Double neLng
) {
    if (swLat != null && swLng != null && neLat != null && neLng != null) {
        return mapService.getMarkersDedupInBounds(swLat, swLng, neLat, neLng);
    }
    return mapService.getMarkersDedupByRoadAddress();
}
   */
}
