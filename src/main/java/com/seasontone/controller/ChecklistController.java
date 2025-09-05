package com.seasontone.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.seasontone.Entity.UserRecord;
import com.seasontone.dto.ChecklistCreateRequest;
import com.seasontone.dto.ChecklistItemDto;
import com.seasontone.dto.ChecklistUpdateRequest;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.security.AuthUser;
import com.seasontone.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChecklistController {
  private final ChecklistService checklistService;

  //체크리스트 생성
  @PostMapping("/checklists")
  @ResponseStatus(HttpStatus.CREATED)
  public ChecklistResponse create(@RequestBody @Valid ChecklistCreateRequest request) {
    return checklistService.create(request);   // ← request 타입이 위 DTO여야 함
  }

  //체크리스트 id별로 찾음.
  @GetMapping("/checklists/{checkId}")
  public ChecklistResponse get(@PathVariable Long checkId) {
    return checklistService.get(checkId);
  }

  //유저별로 체크리스트 보여줌
  @GetMapping(value = "/users/{userId}/checklists/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<ChecklistResponse> pageByUser(
      @PathVariable Long userId,
      @PageableDefault(size = 10, sort = "createdAt", direction = DESC)
      Pageable pageable
  ) {
    return checklistService.pageByUser(userId, pageable);
  }

//보안걸고.. 유저 update, delete
  //모든 요소 같이 업데이트
  @PutMapping(value = "/checklists/{checkId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ChecklistResponse updateAll(@PathVariable Long checkId,
      @AuthenticationPrincipal AuthUser me,
      @RequestBody @Valid ChecklistUpdateRequest req) {
    if (me == null) throw new AccessDeniedException("Login required.");
    return checklistService.updateAllOwned(checkId, me.id(), req);
  }

  //삭제
  @DeleteMapping("/checklists/{checkId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @AuthenticationPrincipal AuthUser me) {
    if (me == null) throw new AccessDeniedException("Login required.");
    checklistService.deleteOwned(checkId, me.id());
  }

/*
  //모든 요소 같이 업데이트
  @PutMapping(value="/checklists/{checkId}", consumes = "application/json", produces = "application/json")
  public ChecklistResponse updateAll(@PathVariable Long checkId,
      @RequestBody @Valid ChecklistUpdateRequest req,
      @RequestHeader("X-USER-ID") Long userId) {
    return checklistService.updateAllOwned(checkId, userId, req);
  }

  //삭제
  @DeleteMapping("/checklists/{checkId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId,
      @RequestHeader("X-USER-ID") Long userId) {
    checklistService.deleteOwned(checkId, userId);
  }

  //매물별 체크리스트..
  @GetMapping("/listings/{listingId}/checklists/page")
  public Page<ChecklistResponse> previewByListingPaged(
      @PathVariable Long listingId, @PageableDefault(size=10, sort="createdAt", direction = DESC) Pageable p) {
    return checklistService.pageByListing(listingId, p);
  }

 */

}
