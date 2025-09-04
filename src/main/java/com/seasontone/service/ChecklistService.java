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

  /** PUT /api/checklists/{checkId}/items */
  @Transactional
  public ChecklistResponse updateItems(Long checkId, ChecklistItemDto dto) {
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found: " + checkId));

    if (r.getItems() == null) r.attachBlankItems();
    applyItems(r.getItems(), dto);

    return toResponse(r);
  }

  /** DELETE /api/checklists/{checkId} */
  @Transactional
  public void delete(Long id) {
    userRecordRepository.deleteById(id);
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