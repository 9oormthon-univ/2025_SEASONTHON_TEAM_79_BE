package com.seasontone.service;

import com.seasontone.domain.ChecklistItems;
import com.seasontone.domain.Listing;
import com.seasontone.domain.User;
import com.seasontone.domain.UserRecord;
import com.seasontone.dto.ChecklistCreateRequest;
import com.seasontone.dto.ChecklistItemDto;
import com.seasontone.dto.ChecklistUpdateRequest;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.repository.ListingRepository;
import com.seasontone.repository.UserRecordRepository;
import com.seasontone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
  public ChecklistResponse updateAllOwned(Long checkId, Long currentUserId, ChecklistUpdateRequest req) {
    UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("You are not the owner of this checklist."));

    // ----- 부모(UserRecord) 필드 교체 -----
    r.setTitle(req.title());
    r.setNotes(req.notes());

    // listingId 처리: null 이면 연결 해제, 값 있으면 재연결
    if (req.listingId() == null) {
      r.setListing(null);
    } else {
      Listing listing = listingRepository.findById(req.listingId())
          .orElseThrow(() -> new EntityNotFoundException("Listing not found: " + req.listingId()));
      r.setListing(listing);
    }

    // ----- 자식(ChecklistItems) 전체 교체 -----
    if (req.items() != null) {
      if (r.getItems() == null) r.attachBlankItems();
      replaceItems(r.getItems(), req.items());   // null 값도 그대로 반영(전체 교체)
    } else {
      // 정책: req.items()가 null이면 items는 그대로 두기
      // 전체 교체를 엄격히 원하면 여기서도 비우거나 새로 초기화 가능
    }

    return toResponse(r);
  }

  /** null 포함 전체 교체 */
  private void replaceItems(ChecklistItems items, ChecklistItemDto dto) {
    items.setName(dto.name());
    items.setAddress(dto.address());
    items.setMonthly(dto.monthly());
    // 점수 필드들(네 필드명 기준으로 교체)
    items.setMining(dto.mining());               // 채광
    items.setWater(dto.water());
    items.setCleanliness(dto.cleanliness());
    items.setOptions(dto.options());
    items.setSecurity(dto.security());
    items.setNoise(dto.noise());
    items.setSurroundings(dto.surroundings());
    items.setRecycling(dto.recycling());
    // 옵션/메모
    items.setElevator(dto.elevator());
    items.setVeranda(dto.veranda());
    items.setPet(dto.pet());
    items.setMemo(dto.memo());
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

  //반올림 해주는 함수
  private static double round1(double v) {
    return Math.round(v * 10.0) / 10.0; // 둘째 자리에서 반올림
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
    //계산결과 받아옴
    double avg = (i != null) ? round1(i.averageScore()) : 0.0;

    return new ChecklistResponse(
        r.getId(),
        r.getUser() != null ? r.getUser().getId() : null,
        r.getListing() != null ? r.getListing().getId() : null,
        r.getTitle(),
        r.getNotes(),
        r.getCreatedAt(),
        r.getUpdatedAt(),
        avg,
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