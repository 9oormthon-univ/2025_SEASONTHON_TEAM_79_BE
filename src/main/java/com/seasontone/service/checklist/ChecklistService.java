package com.seasontone.service.checklist;


import com.seasontone.domain.checklists.RecordVoiceNote;
import com.seasontone.domain.listing.Listing;
import com.seasontone.dto.checklists.ChecklistCreateRequest;
import com.seasontone.dto.checklists.ChecklistItemDto;
import com.seasontone.dto.checklists.ChecklistUpdateRequest;
import com.seasontone.dto.photo.PhotoDto;
import com.seasontone.dto.response.ChecklistPreviewResponse;
import com.seasontone.dto.response.ChecklistGroupResponse;
import com.seasontone.dto.response.ChecklistResponse;
import com.seasontone.dto.response.MyChecklistResponse;
import com.seasontone.domain.checklists.ChecklistItems;
import com.seasontone.domain.users.User;
import com.seasontone.repository.ChecklistItemsRepository;
import com.seasontone.repository.RecordPhotoRepository;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.repository.user.UserRepository;
import com.seasontone.service.listing.ListingService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChecklistService {

  private final UserRepository userRepository;
  private final ChecklistItemsRepository itemsRepo;
  private final RecordPhotoRepository photoRepo;
  private final RecordVoiceNoteRepository voiceRepo;
  private final VoiceNoteService voiceNoteService;
  private final ListingService listingService;

  /*
  @Transactional
  public ChecklistResponse create(ChecklistCreateRequest req) {
    User user = userRepository.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userId()));

    ChecklistItems i = new ChecklistItems();
    i.setUser(user);
    if (req.items() != null) applyItems(i, req.items());

    return toResponse(itemsRepo.save(i));
  }

   */

  // ★ 신규 오버로드: 음성파일 동시 처리
  @Transactional
  public ChecklistResponse create(ChecklistCreateRequest req,
      @Nullable MultipartFile voiceFile,
      @Nullable Integer durationSec) throws IOException {
    User user = userRepository.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userId()));

    Listing listing = listingService.createListing(req.items().address(), req.items().name());

    ChecklistItems i = new ChecklistItems();
    i.setUser(user);
    if (req.items() != null) applyItems(i, req.items(), listing);

    ChecklistItems saved = itemsRepo.save(i); // checkId 확정

    // 파일이 있으면 즉시 업로드(1:1 교체) → Whisper/요약은 VoiceNoteService에서 처리
    if (voiceFile != null && !voiceFile.isEmpty()) {
      voiceNoteService.uploadReplace(saved.getId(), user.getId(), voiceFile, durationSec);
    }
    // 방금 저장된 음성 메타까지 포함해서 반환
    return toResponse(saved);
  }

  public ChecklistResponse get(Long checkId) {
    ChecklistItems i = itemsRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found: " + checkId));
    return toResponse(i);
  }

  public Page<ChecklistResponse> pageByUser(Long userId, Pageable pageable) {
    return itemsRepo.findByUser_Id(userId, pageable).map(this::toResponse);
  }

  public List<MyChecklistResponse> getMyChecklists(User user){
    User u = userRepository.findById(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<ChecklistItems> items = itemsRepo.findByUser(u);

    return items.stream()
        .map(ci -> MyChecklistResponse.builder()
            .id(ci.getId())
            .aptNm(ci.getName())
            .address(ci.getAddress())
            .deposit(ci.getDeposit() == null ? 0 : ci.getDeposit())
            .monthly(ci.getMonthly() == null ? 0 : ci.getMonthly())
            .maintenanceFee(ci.getMaintenanceFee() == null ? 0 : ci.getMaintenanceFee())
            .floorAreaSqm(ci.getFloorAreaSqm() == null ? 0 : ci.getFloorAreaSqm())
            .avgScore(round1(ci.averageScore()))
            .photos(loadPhotoMetas(ci.getId()))
            .build())
        .toList();
  }

  @Transactional
  public ChecklistResponse updateAllOwned(Long checkId, Long currentUserId, ChecklistUpdateRequest req) {
    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("You are not the owner."));
    if (req.items() != null) replaceItems(i, req.items());
    return toResponse(i);
  }

  @Transactional
  public void deleteOwned(Long checkId, Long currentUserId) {
    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    itemsRepo.delete(i);
  }

  private void applyItems(ChecklistItems i, ChecklistItemDto d, Listing listing){
    i.setName(d.name());
    i.setAddress(d.address());
    i.setMonthly(d.monthly());
    i.setDeposit(d.deposit());
    i.setMaintenanceFee(d.maintenanceFee());
    i.setFloorAreaSqm(d.floorAreaSqm());
    i.setMining(d.mining());
    i.setWater(d.water());
    i.setCleanliness(d.cleanliness());
    i.setOptions(d.options());
    i.setSecurity(d.security());
    i.setNoise(d.noise());
    i.setSurroundings(d.surroundings());
    i.setRecycling(d.recycling());
    i.setElevator(d.elevator());
    i.setVeranda(d.veranda());
    i.setPet(d.pet());
    i.setMemo(d.memo());
    i.setListing(listing);
  }

  private void replaceItems(ChecklistItems i, ChecklistItemDto d){
    // 전체 교체 정책(Null도 그대로 반영)
    i.setName(d.name());
    i.setAddress(d.address());
    i.setMonthly(d.monthly());
    i.setDeposit(d.deposit());
    i.setMaintenanceFee(d.maintenanceFee());
    i.setFloorAreaSqm(d.floorAreaSqm());
    i.setMining(d.mining());
    i.setWater(d.water());
    i.setCleanliness(d.cleanliness());
    i.setOptions(d.options());
    i.setSecurity(d.security());
    i.setNoise(d.noise());
    i.setSurroundings(d.surroundings());
    i.setRecycling(d.recycling());
    i.setElevator(d.elevator());
    i.setVeranda(d.veranda());
    i.setPet(d.pet());
    i.setMemo(d.memo());
  }

  //평점 소수점 1자리까지 출력
  private static Double round1(Double v) {
    if (v == null) return null;
    return Math.round(v * 10.0) / 10.0;
  }

  private ChecklistResponse toResponse(ChecklistItems i) {
    String voiceSummary = voiceRepo.findByItems_Id(i.getId())
        .map(RecordVoiceNote::getSummary)
        .orElse(null);

    var itemsDto = new ChecklistItemDto(
        i.getName(), i.getAddress(), i.getMonthly(), i.getDeposit(), i.getMaintenanceFee(), i.getFloorAreaSqm(),
        i.getMining(), i.getWater(), i.getCleanliness(), i.getOptions(), i.getSecurity(), i.getNoise(),
        i.getSurroundings(), i.getRecycling(), i.getElevator(), i.getVeranda(), i.getPet(), i.getMemo(), i.getListing(),
        voiceSummary // 여기에 넣는다. null이면 응답에서 자동 생략됨
    );
    Double avg = round1(i.averageScore());

    // photos (1:N)
    List<PhotoDto> photos = photoRepo.findByItems_Id(i.getId()).stream()
        .map(p -> new PhotoDto(p.getId(), p.getFilename(), p.getContentType(), p.getSize(),
            p.getCaption(), p.getCreatedAt(),
            "/api/checklists/%d/photos/%d/raw".formatted(i.getId(), p.getId())))
        .toList();

    return new ChecklistResponse(
        i.getId(),
        i.getUser() != null ? i.getUser().getId() : null,
        avg,
        itemsDto,
        photos
    );
  }

  //주소 + 가장 최근의 월세, 보증금, 관리비, 점수로 묶어서 리스트
  //둘러보기 목록 조회
  public List<ChecklistGroupResponse> getGroupedByAddress() {
    List<ChecklistItems> checklists = itemsRepo.findAll();

    //주소 null값일시 반환x
    Map<String, List<ChecklistItems>> grouped = checklists.stream()
        .filter(ci -> ci.getAddress() != null && !ci.getAddress().isBlank())
        .collect(Collectors.groupingBy(ChecklistItems::getAddress));

    return grouped.entrySet().stream()
            .map(entry -> {
              List<ChecklistItems> groupList = entry.getValue();

              // 각 그룹에서 가장 최근 생성된 체크리스트 가져오기
              ChecklistItems latest = groupList.stream()
                      .max(Comparator.comparing(ChecklistItems::getId))
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
                                      .score(round1(checklist.averageScore()))
                                      .build())
                              .toList())
                      .build();
            })
            .toList();
  }

  public List<ChecklistGroupResponse> getRegionChecklist(User user) {
    User findUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));
    List<ChecklistItems> checklists = itemsRepo.findByAddressContaining(findUser.getRegion());

    Map<String, List<ChecklistItems>> grouped = checklists.stream()
        .collect(Collectors.groupingBy(ChecklistItems::getAddress));

    return grouped.entrySet().stream()
        .map(entry -> {
          List<ChecklistItems> groupList = entry.getValue();

          // 각 그룹에서 가장 최근 생성된 체크리스트 가져오기
          ChecklistItems latest = groupList.stream()
              .max(Comparator.comparing(ChecklistItems::getId))
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
                      .score(round1(checklist.averageScore()))
                      .photos(loadPhotoMetas(checklist.getId()))
                      .build())
                  .toList())
              .build();
        })
        .toList();
  }

  //api/checklists 검색요청
  public List<ChecklistPreviewResponse> searchGroupedSummary(String query) {
    String key = (query == null) ? "" : query.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    if (key.isBlank()) return List.of(); // 빈 검색어면 빈 결과(정책에 따라 전체 반환도 가능)

    List<ChecklistItems> matched = itemsRepo.searchByNameOrAddressNoSpace(key);

    // address가 비어있는 건 제외(그룹 키 null 방지)
    Map<String, List<ChecklistItems>> grouped = matched.stream()
        .filter(ci -> ci.getAddress() != null && !ci.getAddress().isBlank())
        .collect(Collectors.groupingBy(ChecklistItems::getAddress));

    return grouped.entrySet().stream()
        .map(entry -> {
          List<ChecklistItems> group = entry.getValue();

          // "가장 마지막 값" 기준: id 최대
          ChecklistItems latest = group.stream()
              .max(Comparator.comparing(ChecklistItems::getId))
              .orElseThrow();

          // 그룹 평균 점수(소수 1자리)
          double avg = round1(
              group.stream().mapToDouble(ChecklistItems::averageScore).average().orElse(0.0)
          );

          return ChecklistPreviewResponse.builder()
              .address(entry.getKey())
              .latestName(safeStr(latest.getName()))
              .latestMonthly(nz(latest.getMonthly()))
              .latestDeposit(nz(latest.getDeposit()))
              .latestMaintenanceFee(nz(latest.getMaintenanceFee()))
              .latestFloorAreaSqm(nz(latest.getFloorAreaSqm()))
              .avgScore(avg)
              .build();
        })
        .sorted(Comparator.comparingDouble(ChecklistPreviewResponse::getAvgScore).reversed()) // 점수 높은 순
        .toList();
  }

  private List<PhotoDto> loadPhotoMetas(Long itemsId) {
    return photoRepo.findByItems_Id(itemsId).stream()
        .map(m -> new PhotoDto(
            m.getId(),
            m.getFilename(),
            m.getContentType(),
            m.getSize(),
            m.getCaption(),
            m.getCreatedAt(),
            // 기존 라우팅 규칙 유지
            "/api/checklists/%d/photos/%d/raw".formatted(itemsId, m.getId())
        ))
        .toList();
  }

  private static int nz(Integer v) { return v == null ? 0 : v; }
  private static String safeStr(String s) { return (s == null || s.isBlank()) ? "" : s; }

}
