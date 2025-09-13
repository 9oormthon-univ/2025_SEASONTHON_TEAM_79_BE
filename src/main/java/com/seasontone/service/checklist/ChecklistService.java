package com.seasontone.service.checklist;


import com.seasontone.dto.checklists.ChecklistCreateRequest;
import com.seasontone.dto.checklists.ChecklistItemDto;
import com.seasontone.dto.checklists.ChecklistUpdateRequest;
import com.seasontone.dto.photo.PhotoDto;
import com.seasontone.dto.voice.VoiceNoteDto;
import com.seasontone.dto.response.ChecklistGroupResponse;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.dto.response.MyChecklistResponse;
import com.seasontone.domain.checklists.ChecklistItems;
import com.seasontone.domain.checklists.Listing;
import com.seasontone.domain.checklists.UserRecord;
import com.seasontone.domain.users.User;
import com.seasontone.repository.ChecklistItemsRepository;
import com.seasontone.repository.ListingRepository;
import com.seasontone.repository.RecordPhotoRepository;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.repository.UserRecordRepository;
import com.seasontone.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
  private final RecordPhotoRepository photoRepo;
  private final RecordVoiceNoteRepository voiceRepo;
  private final ChecklistItemsRepository checklistItemsRepository;

  // POST /api/checklists
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

  //** GET /api/checklists/{checkId}
  public ChecklistResponse get(Long checkId) {
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found: " + checkId));
    return toResponse(r);
  }

  //** GET /api/users/{userId}/checklists
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

  //null 포함 전체 교체
  private void replaceItems(ChecklistItems items, ChecklistItemDto dto) {
    items.setName(dto.name());
    items.setAddress(dto.address());
    items.setMonthly(dto.monthly());
    items.setDeposit(dto.deposit());
    items.setMaintenanceFee(dto.maintenanceFee());
    items.setFloorAreaSqm(dto.floorAreaSqm());
    // 점수 필드들(네 필드명 기준으로 교체)
    items.setMining(dto.mining());// 채광
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
    var r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    userRecordRepository.delete(r); // cascade/orphanRemoval로 하위 전부 제거
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

    // 기존 itemsDto/avgScore 계산은 그대로...
    ChecklistItemDto itemsDto = (i == null) ? null : new ChecklistItemDto(
        i.getName(), i.getAddress(), i.getMonthly(), i.getDeposit(), i.getMaintenanceFee(), i.getFloorAreaSqm(),
        i.getMining(), i.getWater(), i.getCleanliness(),
        i.getOptions(), i.getSecurity(), i.getNoise(),
        i.getSurroundings(), i.getRecycling(),
        i.getElevator(), i.getVeranda(), i.getPet(), i.getMemo()
    );

    Double avg = (i == null) ? null : i.averageScore(); // 네가 만든 평균(소수1자리 반올림) 사용

    // ★ 추가: 사진 메타 조회 (items 없으면 빈 리스트)
    List<PhotoDto> photos = (i == null) ? List.of() :
        photoRepo.findMetaByItems_Id(i.getId()).stream()
            .map(m -> new PhotoDto(
                m.getId(), m.getFilename(), m.getContentType(), m.getSize(),
                m.getCaption(), m.getCreatedAt(),
                "/api/checklists/%d/photos/%d/raw".formatted(r.getId(), m.getId())
            ))
            .toList();

    // ★ 추가: 음성 메타 조회 (있으면 1개)
    VoiceNoteDto voice = null;
    if (i != null) {
      voice = voiceRepo.findByItems_Id(i.getId())
          .map(v -> new VoiceNoteDto(
              v.getFilename(), v.getContentType(), v.getSize(), v.getDurationSec(),
              v.getTranscript(), v.getSummary(), v.getCreatedAt(), v.getUpdatedAt(),
              "/api/checklists/%d/audio/raw".formatted(r.getId())
          ))
          .orElse(null);
    }

    return new ChecklistResponse(
        r.getId(),
        r.getUser() != null ? r.getUser().getId() : null,
        r.getListing() != null ? r.getListing().getId() : null,
        r.getTitle(),
        r.getNotes(),
        r.getCreatedAt(),
        r.getUpdatedAt(),
        avg,
        itemsDto,
        photos,   // ← 추가
        voice     // ← 추가
    );
  }

  //매물별로 체크리스트 보기
  public Page<ChecklistResponse> pageByListing(Long listingId, Pageable pageable) {
    // if (!listingRepository.existsById(listingId)) throw new EntityNotFoundException(...);
    return userRecordRepository.findByListing_Id(listingId, pageable)
        .map(this::toResponse);
  }

  //나의 기록 목록 조회
  public List<MyChecklistResponse> getMyChecklists(User user){
    User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));
    List<UserRecord> findUserRecords = userRecordRepository.findByUser(findUser);

    return findUserRecords.stream()
            .map(userRecord -> {
              ChecklistItems checklist = userRecord.getItems();
              return MyChecklistResponse.builder()
                      .id(checklist.getId())
                      .aptNm(checklist.getName())
                      .address(checklist.getAddress())
                      .deposit(checklist.getDeposit())
                      .monthly(checklist.getMonthly())
                      .maintenanceFee(checklist.getMaintenanceFee())
                      .floorAreaSqm(checklist.getFloorAreaSqm())
                      .avgScore(checklist.averageScore())
                      .build();
            })
            .toList();
  }

  //주소 + 가장 최근의 월세, 보증금, 관리비, 점수로 묶어서 리스트
  //둘러보기 목록 조회
  public List<ChecklistGroupResponse> getGroupedByAddress() {
    List<ChecklistItems> checklists = checklistItemsRepository.findAll();

    Map<String, List<ChecklistItems>> grouped = checklists.stream()
            .collect(Collectors.groupingBy(ChecklistItems::getAddress));

    return grouped.entrySet().stream()
            .map(entry -> {
              List<ChecklistItems> groupList = entry.getValue();

              // 각 그룹에서 가장 최근 생성된 체크리스트 가져오기
              ChecklistItems latest = groupList.stream()
                      .max(Comparator.comparing(ChecklistItems::getCreatedAt))
                      .orElseThrow();

              double avgScore = groupList.stream()
                      .mapToDouble(ChecklistItems::averageScore)
                      .average()
                      .orElse(0.0);

              return ChecklistGroupResponse.builder()
                      .address(entry.getKey())
                      .latestName(latest.getName())
                      .latestMonthly(latest.getMonthly())
                      .latestDeposit(latest.getDeposit())
                      .avgScore(avgScore)
                      .latestMaintenanceFee(latest.getMaintenanceFee())
                      .latestFloorAreaSqm(latest.getFloorAreaSqm())
                      .checklists(groupList.stream()
                              .map(checklist -> ChecklistGroupResponse.ChecklistDetailsResponse.builder()
                                      .id(checklist.getId())
                                      .name(checklist.getName())
                                      .monthly(checklist.getMonthly())
                                      .deposit(checklist.getDeposit())
                                      .maintenanceFee(checklist.getMaintenanceFee())
                                      .floorAreaSqm(checklist.getFloorAreaSqm())
                                      .score(checklist.averageScore())
                                      .build())
                              .toList())
                      .build();
            })
            .toList();
  }

  public List<ChecklistGroupResponse> getRegionChecklist(User user) {
    User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));
    List<ChecklistItems> checklists = checklistItemsRepository.findByAddressContaining(findUser.getRegion());

    Map<String, List<ChecklistItems>> grouped = checklists.stream()
            .collect(Collectors.groupingBy(ChecklistItems::getAddress));

    return grouped.entrySet().stream()
            .map(entry -> {
              List<ChecklistItems> groupList = entry.getValue();

              // 각 그룹에서 가장 최근 생성된 체크리스트 가져오기
              ChecklistItems latest = groupList.stream()
                      .max(Comparator.comparing(ChecklistItems::getCreatedAt))
                      .orElseThrow();

              double avgScore = groupList.stream()
                      .mapToDouble(ChecklistItems::averageScore)
                      .average()
                      .orElse(0.0);


              return ChecklistGroupResponse.builder()
                      .address(entry.getKey())
                      .latestName(latest.getName())
                      .latestMonthly(latest.getMonthly())
                      .latestDeposit(latest.getDeposit())
                      .avgScore(avgScore)
                      .latestMaintenanceFee(latest.getMaintenanceFee())
                      .latestFloorAreaSqm(latest.getFloorAreaSqm())
                      .checklists(groupList.stream()
                              .map(checklist -> ChecklistGroupResponse.ChecklistDetailsResponse.builder()
                                      .id(checklist.getId())
                                      .name(checklist.getName())
                                      .monthly(checklist.getMonthly())
                                      .deposit(checklist.getDeposit())
                                      .maintenanceFee(checklist.getMaintenanceFee())
                                      .floorAreaSqm(checklist.getFloorAreaSqm())
                                      .score(checklist.averageScore())
                                      .build())
                              .toList())
                      .build();
            })
            .toList();
  }
}
