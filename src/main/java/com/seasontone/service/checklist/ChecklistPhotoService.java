package com.seasontone.service.checklist;

import com.seasontone.domain.checklists.ChecklistItems;
import com.seasontone.dto.photo.PhotoDto;
import com.seasontone.domain.checklists.RecordPhoto;
import com.seasontone.repository.ChecklistItemsRepository;
import com.seasontone.repository.RecordPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistPhotoService {

  private final RecordPhotoRepository photoRepo;
  private final ChecklistItemsRepository itemsRepo;

  private static final long MAX_SIZE = 10 * 1024 * 1024;
  private static final List<String> ALLOWED_TYPES =
      List.of("image/jpeg", "image/png", "image/webp");

  @Transactional
  public PhotoDto upload(Long checkId, Long currentUserId,
      MultipartFile file, String caption) {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
    if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("file too large");
    if (file.getContentType()==null ||
        ALLOWED_TYPES.stream().noneMatch(t -> t.equalsIgnoreCase(file.getContentType())))
      throw new IllegalArgumentException("unsupported content-type");

    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));

    RecordPhoto p = new RecordPhoto();
    p.setItems(i);
    p.setFilename(file.getOriginalFilename());
    p.setContentType(file.getContentType());
    p.setSize(file.getSize());
    p.setCaption(caption);
    try {
      p.setData(file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("failed to read file bytes", e);
    }
    p.setCreatedAt(Instant.now());

    RecordPhoto saved = photoRepo.saveAndFlush(p);

    String rawUrl = "/api/checklists/%d/photos/%d/raw".formatted(i.getId(), saved.getId());
    return new PhotoDto(saved.getId(), saved.getFilename(), saved.getContentType(),
        saved.getSize(), saved.getCaption(), saved.getCreatedAt(), rawUrl);
  }

  // 1:N 목록 조회
  @Transactional(readOnly = true)
  public List<PhotoDto> list(Long checkId) {
    ChecklistItems i = itemsRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    return photoRepo.findByItems_Id(i.getId()).stream()
        .map(p -> new PhotoDto(
            p.getId(), p.getFilename(), p.getContentType(), p.getSize(),
            p.getCaption(), p.getCreatedAt(),
            "/api/checklists/%d/photos/%d/raw".formatted(i.getId(), p.getId())
        ))
        .toList();
  }

  //원본 바이트: checkId + photoId
  @Transactional(readOnly = true)
  public RecordPhoto loadEntityForRaw(Long checkId, Long photoId) {
    ChecklistItems i = itemsRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    return photoRepo.findByIdAndItems_Id(photoId, i.getId())
        .orElseThrow(() -> new EntityNotFoundException("Photo not found"));
  }

  //특정 사진 삭제
  @Transactional
  public void delete(Long checkId, Long photoId, Long currentUserId) {
    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));
    long deleted = photoRepo.deleteByIdAndItems_Id(photoId, i.getId());
    if (deleted == 0) throw new EntityNotFoundException("Photo not found");
  }
}
