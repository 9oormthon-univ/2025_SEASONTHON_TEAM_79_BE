package com.seasontone.service;

import com.seasontone.domain.ChecklistItems;
import com.seasontone.domain.Listing;
import com.seasontone.domain.User;
import com.seasontone.domain.UserRecord;
import com.seasontone.dto.ChecklistCreateRequest;
import com.seasontone.dto.ChecklistItemDto;
import com.seasontone.dto.RecordCreateRequest;
import com.seasontone.dto.RecordUpdateRequest;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.dto.response.RecordDetailResponse;
import com.seasontone.dto.response.RecordSummaryResponse;
import com.seasontone.repository.ListingRepository;
import com.seasontone.repository.UserRecordRepository;
import com.seasontone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChecklistService {

  private final UserRepository userRepository;
  private final ListingRepository listingRepository;
  private final UserRecordRepository userRecordRepository;

  /** POST /api/checklists */
  @Transactional
  public ChecklistResponse create(ChecklistCreateRequest req) {
    // 1) 작성자
    User user = userRepository.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userId()));

    // 2) 매물(선택)
    Listing listing = null;
    if (req.listingId() != null) {
      listing = listingRepository.findById(req.listingId())
          .orElseThrow(() -> new EntityNotFoundException("Listing not found: " + req.listingId()));
    }

    // 3) 체크리스트 + 폼 (builder 대신 new + setter)
    UserRecord record = new UserRecord();
    record.setUser(user);
    record.setListing(listing);
    record.setTitle(req.title());
    record.setNotes(req.notes());

    record.attachBlankItems();
    if (req.items() != null) {
      applyItems(record.getItems(), req.items());
    }

    UserRecord saved = userRecordRepository.save(record);
    return toResponse(saved);
  }

  /** GET /api/checklists/{checkId} */
  public ChecklistResponse get(Long checkId) {
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found: " + checkId));
    return toResponse(r);
  }

  /** GET /api/users/{userId}/checklists */
  public Page<ChecklistResponse> pageByUser(Long userId, Pageable pageable) {
    return userRecordRepository.findByUser_Id(userId, pageable)
        .map(this::toResponse);
  }

  @Transactional
  public ChecklistResponse updateItemsOwned(Long checkId, Long currentUserId, ChecklistItemDto dto) {
    UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("You are not the owner of this checklist."));

    if (r.getItems() == null) r.attachBlankItems();

    // (예) 부모/자식 모두 수정 가능
    if (dto.name() != null)      r.getItems().setName(dto.name());
    if (dto.address() != null)   r.getItems().setAddress(dto.address());
    if (dto.monthly() != null)   r.getItems().setMonthly(dto.monthly());
    if (dto.water() != null)     r.getItems().setWater(dto.water());
    if (dto.cleanliness() != null) r.getItems().setCleanliness(dto.cleanliness());
    if (dto.options() != null)   r.getItems().setOptions(dto.options());
    if (dto.security() != null)  r.getItems().setSecurity(dto.security());
    if (dto.noise() != null)     r.getItems().setNoise(dto.noise());
    if (dto.surroundings() != null) r.getItems().setSurroundings(dto.surroundings());
    if (dto.elevator() != null)  r.getItems().setElevator(dto.elevator());
    if (dto.veranda() != null)   r.getItems().setVeranda(dto.veranda());
    if (dto.pet() != null)       r.getItems().setPet(dto.pet());
    if (dto.memo() != null)      r.getItems().setMemo(dto.memo());

    // 부모 필드도 필요하면 같이 수정하기
    // if (dto.title() != null) r.setTitle(dto.title());  // DTO에 있으면
    // if (dto.notes() != null) r.setNotes(dto.notes());

    return toResponse(r); // 영속 상태라 트랜잭션 커밋 시 자동 flush
  }

  @Transactional
  public void deleteOwned(Long checkId, Long currentUserId) {
    long deleted = userRecordRepository.deleteByIdAndUser_Id(checkId, currentUserId);
    if (deleted == 0) throw new AccessDeniedException("You are not the owner or record not found.");
    // ★ 여기서 부모 삭제 → cascade/orphanRemoval에 의해 자식도 같이 삭제됨
  }

  // ---------- helpers ----------
  private void applyItems(ChecklistItems items, ChecklistItemDto dto) {
    items.setName(dto.name());
    items.setAddress(dto.address());
    items.setMonthly(dto.monthly());
    items.setWater(dto.water());
    items.setCleanliness(dto.cleanliness());
    items.setOptions(dto.options());
    items.setSecurity(dto.security());
    items.setNoise(dto.noise());
    items.setSurroundings(dto.surroundings());
    items.setElevator(dto.elevator());
    items.setVeranda(dto.veranda());
    items.setPet(dto.pet());
    items.setMemo(dto.memo());
  }

  private ChecklistResponse toResponse(UserRecord r) {
    ChecklistItems i = r.getItems();
    ChecklistItemDto itemsDto = (i == null)
        ? null
        : new ChecklistItemDto(
            i.getName(),
            i.getAddress(),
            i.getMonthly(),
            i.getMining(),
            i.getWater(),
            i.getCleanliness(),
            i.getOptions(),
            i.getSecurity(),
            i.getNoise(),
            i.getSurroundings(),
            i.getRecycling(),
            i.getElevator(),
            i.getVeranda(),
            i.getPet(),
            i.getMemo()
        );

    return new ChecklistResponse(
        r.getId(),
        r.getUser() != null ? r.getUser().getId() : null,
        r.getListing() != null ? r.getListing().getId() : null,
        r.getTitle(),
        r.getNotes(),
        r.getCreatedAt(),
        r.getUpdatedAt(),
        itemsDto
    );
  }

  //매물별로 체크리스트 보기
  public Page<ChecklistResponse> pageByListing(Long listingId, Pageable pageable) {
    // if (!listingRepository.existsById(listingId)) throw new EntityNotFoundException(...);
    return userRecordRepository.findByListing_Id(listingId, pageable)
        .map(this::toResponse);
  }
}