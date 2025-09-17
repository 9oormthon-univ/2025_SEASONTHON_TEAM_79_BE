package com.seasontone.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasontone.dto.checklists.ChecklistCreateRequest;
import com.seasontone.dto.checklists.ChecklistUpdateRequest;
import com.seasontone.dto.response.ChecklistGroupResponse;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.dto.response.MyChecklistResponse;
import com.seasontone.domain.users.User;
import com.seasontone.service.checklist.ChecklistService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChecklistController {
  private final ChecklistService checklistService;
  private final ObjectMapper objectMapper;

  /*
  //체크리스트 생성
  @PostMapping("/checklists")
  @ResponseStatus(HttpStatus.CREATED)
  public ChecklistResponse create(@RequestBody @Valid ChecklistCreateRequest request) {
    return checklistService.create(request);   // ← request 타입이 위 DTO여야 함
  }

   */

  //체크리스트 생성
  @PostMapping("/checklists")
  @ResponseStatus(HttpStatus.CREATED)
  public ChecklistResponse createMultipart(
      @RequestParam("payload") String payloadJson,                // ← String으로 받기!
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestParam(value = "durationSec", required = false) Integer durationSec,
      @AuthenticationPrincipal(expression="id") Long meId
  ) throws IOException {
    if (meId == null) throw new AccessDeniedException("Login required.");

    ChecklistCreateRequest payload =
        objectMapper.readValue(payloadJson, ChecklistCreateRequest.class);

    if (!payload.userId().equals(meId)) throw new AccessDeniedException("Not owner.");

    return checklistService.create(payload, file, durationSec);
  }

  //체크리스트 id별로 찾음.
  @GetMapping("/checklists/{checkId}")
  public ChecklistResponse get(@PathVariable Long checkId) {
    return checklistService.get(checkId);
  }

  //유저별로 체크리스트 보여줌 (전부 보여주는 api) = checklists/mine은 미리보기
  @GetMapping(value = "/users/{userId}/checklists/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<ChecklistResponse> pageByUser(
      @PathVariable Long userId,
      @PageableDefault(size = 10, direction = DESC)
      Pageable pageable
  ) {
    return checklistService.pageByUser(userId, pageable);
  }

  //모든 요소 같이 업데이트
  @PutMapping(value = "/checklists/{checkId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ChecklistResponse updateAll(@PathVariable Long checkId,
      @AuthenticationPrincipal User me,
      @RequestBody @Valid ChecklistUpdateRequest req) {
    if (me == null) throw new AccessDeniedException("Login required.");
    return checklistService.updateAllOwned(checkId, me.getId(), req);
  }

  //삭제
  @DeleteMapping("/checklists/{checkId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @AuthenticationPrincipal User me) {
    if (me == null) throw new AccessDeniedException("Login required.");
    checklistService.deleteOwned(checkId, me.getId());
  }

  @GetMapping("/checklists/region")
  public ResponseEntity<List<ChecklistGroupResponse>> getRegionChecklist(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(checklistService.getRegionChecklist(user));
  }

  @GetMapping("/checklists/mine")
  public ResponseEntity<List<MyChecklistResponse>> getMyChecklists(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(checklistService.getMyChecklists(user));
  }

  @GetMapping("/checklists")
  public ResponseEntity<List<ChecklistGroupResponse>> getGroupedByAddress() {
    return ResponseEntity.ok(checklistService.getGroupedByAddress());
  }

}
