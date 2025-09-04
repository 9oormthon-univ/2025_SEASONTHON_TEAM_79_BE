package com.seasontone.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.seasontone.domain.UserRecord;
import com.seasontone.dto.ChecklistCreateRequest;
import com.seasontone.dto.ChecklistItemDto;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.service.ChecklistService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChecklistController {
  private final ChecklistService checklistService;

  @PostMapping("/checklists")
  @ResponseStatus(HttpStatus.CREATED)
  public ChecklistResponse create(@RequestBody @Valid ChecklistCreateRequest request) {
    return checklistService.create(request);   // ← request 타입이 위 DTO여야 함
  }

  @GetMapping("/checklists/{checkId}")
  public ChecklistResponse get(@PathVariable Long checkId) {
    return checklistService.get(checkId);
  }

  @GetMapping(value = "/users/{userId}/checklists/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<ChecklistResponse> pageByUser(
      @PathVariable Long userId,
      @PageableDefault(size = 10, sort = "createdAt", direction = DESC)
      Pageable pageable
  ) {
    return checklistService.pageByUser(userId, pageable);
  }

  @PutMapping("/checklists/{checkId}/items")
  public ChecklistResponse updateItems(@PathVariable Long checkId,
      @RequestBody @Valid ChecklistItemDto dto) {
    return checklistService.updateItems(checkId, dto);
  }

  @DeleteMapping("/checklists/{checkId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId) {
    checklistService.delete(checkId);
  }

  //매물별 체크리스트..
  @GetMapping("/listings/{listingId}/checklists/page")
  public Page<ChecklistResponse> previewByListingPaged(
      @PathVariable Long listingId, @PageableDefault(size=10, sort="createdAt", direction = DESC) Pageable p) {
    return checklistService.pageByListing(listingId, p);
  }

}
