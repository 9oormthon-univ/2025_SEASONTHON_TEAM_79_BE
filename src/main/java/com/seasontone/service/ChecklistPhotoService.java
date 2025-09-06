package com.seasontone.service;

import com.seasontone.dto.PhotoDto;
import com.seasontone.dto.PhotoMetaProjection;
import com.seasontone.entity.RecordPhoto;
import com.seasontone.entity.UserRecord;
import com.seasontone.repository.RecordPhotoRepository;
import com.seasontone.repository.UserRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistPhotoService {

  private final UserRecordRepository userRecordRepository;
  private final RecordPhotoRepository photoRepo;

  private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
  private static final List<String> ALLOWED_TYPES = List.of("image/jpeg","image/png","image/webp");

  @Transactional
    public PhotoDto upload(Long checkId, Long currentUserId, MultipartFile file, String caption){
      if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
      if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("file too large");
      if (file.getContentType()==null || ALLOWED_TYPES.stream().noneMatch(t -> t.equalsIgnoreCase(file.getContentType())))
        throw new IllegalArgumentException("unsupported content-type");

      UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
          .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));
      if (r.getItems()==null){ r.attachBlankItems(); }

      try {
        RecordPhoto p = new RecordPhoto();
        p.setItems(r.getItems());
        p.setFilename(file.getOriginalFilename());
        p.setContentType(file.getContentType());
        p.setSize(file.getSize());
        p.setCaption(caption);
        p.setData(file.getBytes());     // ★ 변환 없이 그대로
        p.setCreatedAt(Instant.now());
        RecordPhoto saved = photoRepo.saveAndFlush(p);
        Long photoId = saved.getId();

        log.info("[PHOTO-SAVED] checkId={}, itemsId={}, photoId={}, bytes={}",
            r.getId(), r.getItems().getId(), saved.getId(), saved.getData().length);

        String rawUrl = "/api/photos/%d".formatted(saved.getId()); // 간단 URL
        return new PhotoDto(saved.getId(), saved.getFilename(), saved.getContentType(),
            saved.getSize(), saved.getCaption(), saved.getCreatedAt(), rawUrl);
      } catch(Exception e){
        throw new RuntimeException("Failed to save photo", e);
      }
  }

  @Transactional(readOnly = true)
  public List<PhotoDto> list(Long checkId){
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    if (r.getItems()==null) return List.of();

    List<PhotoMetaProjection> metas = photoRepo.findMetaByItems_Id(r.getItems().getId());
    return metas.stream()
        .map(m -> new PhotoDto(
            m.getId(),            // ← getter 스타일
            m.getFilename(),
            m.getContentType(),
            m.getSize(),
            m.getCaption(),
            m.getCreatedAt(),
            "/api/photos/%d".formatted(m.getId())
        ))
        .toList();
  }

  @Transactional(readOnly = true)
  public RecordPhoto loadEntityForRawByCheck(Long checkId, Long photoId){
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    if (r.getItems()==null) throw new EntityNotFoundException("Items not found");
    return photoRepo.findByIdAndItems_Id(photoId, r.getItems().getId())
        .orElseThrow(() -> new EntityNotFoundException("Photo not found"));
  }

  @Transactional(readOnly = true)
  public RecordPhoto loadEntityForRawByPhotoId(Long photoId){
    return photoRepo.findById(photoId)
        .orElseThrow(() -> new EntityNotFoundException("Photo not found"));
  }

  @Transactional
  public void delete(Long checkId, Long photoId, Long currentUserId){
    UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));
    if (r.getItems()==null) throw new EntityNotFoundException("Items not found");
    long deleted = photoRepo.deleteByIdAndItems_Id(photoId, r.getItems().getId());
    if (deleted==0) throw new EntityNotFoundException("Photo not found");
  }
}
