package com.seasontone.service;

import com.seasontone.Entity.ChecklistItems;
import com.seasontone.Entity.RecordVoiceNote;
import com.seasontone.Entity.UserRecord;
import com.seasontone.dto.VoiceNoteDto;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.repository.UserRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistAudioService {

  private final UserRecordRepository userRecordRepository;
  private final RecordVoiceNoteRepository voiceRepo;

  private static final long MAX_SIZE = 20 * 1024 * 1024; // 20MB
  private static final String[] ALLOWED = {"audio/mpeg","audio/mp4","audio/x-m4a","audio/wav","audio/webm"};

  @Transactional
  public VoiceNoteDto uploadReplace(Long checkId, Long currentUserId, MultipartFile file, Integer durationSec){
    if (file==null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
    if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("file too large");
    String ct = file.getContentType();
    boolean ok = false; if (ct!=null) for(String t:ALLOWED){ if (t.equalsIgnoreCase(ct)) {ok=true; break;} }
    if (!ok) throw new IllegalArgumentException("unsupported content-type");

    UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));

    ChecklistItems items = r.getItems();
    if (items==null){ r.attachBlankItems(); items = r.getItems(); }

    // 기존 음성 있으면 교체(삭제)
    voiceRepo.findByItems_Id(items.getId()).ifPresent(v -> voiceRepo.delete(v));

    try {
      RecordVoiceNote v = new RecordVoiceNote();
      v.setItems(items);
      v.setFilename(file.getOriginalFilename());
      v.setContentType(ct);
      v.setSize(file.getSize());
      v.setDurationSec(durationSec);
      v.setData(file.getBytes());        // ★ 변환 없이 그대로
      v.setTranscript(null);             // 추후 Whisper
      v.setSummary(null);                // 추후 GPT 요약
      v.setCreatedAt(Instant.now());
      v.setUpdatedAt(null);
      RecordVoiceNote saved = voiceRepo.save(v);

      log.info("[VOICE-SAVED] checkId={}, itemsId={}, bytes={}", checkId, items.getId(), saved.getData().length);

      String rawUrl = "/api/checklists/%d/audio/raw".formatted(checkId);
      return new VoiceNoteDto(saved.getFilename(), saved.getContentType(), saved.getSize(),
          saved.getDurationSec(), saved.getTranscript(), saved.getSummary(),
          saved.getCreatedAt(), saved.getUpdatedAt(), rawUrl);
    } catch(Exception e){
      throw new RuntimeException("Failed to save voice note", e);
    }
  }

  @Transactional(readOnly = true)
  public VoiceNoteDto getMeta(Long checkId){
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    ChecklistItems items = r.getItems();
    if (items==null) throw new EntityNotFoundException("Items not found");
    var v = voiceRepo.findByItems_Id(items.getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice not found"));
    return new VoiceNoteDto(v.getFilename(), v.getContentType(), v.getSize(),
        v.getDurationSec(), v.getTranscript(), v.getSummary(),
        v.getCreatedAt(), v.getUpdatedAt(),
        "/api/checklists/%d/audio/raw".formatted(checkId));
  }

  @Transactional(readOnly = true)
  public RecordVoiceNote loadRaw(Long checkId){
    UserRecord r = userRecordRepository.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    ChecklistItems items = r.getItems();
    if (items==null) throw new EntityNotFoundException("Items not found");
    return voiceRepo.findByItems_Id(items.getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice not found"));
  }

  @Transactional
  public void delete(Long checkId, Long currentUserId){
    UserRecord r = userRecordRepository.findByIdAndUser_Id(checkId, currentUserId)
        .orElseThrow(() -> new AccessDeniedException("Not owner or record not found"));
    ChecklistItems items = r.getItems();
    if (items==null) return;
    voiceRepo.deleteByItems_Id(items.getId());
  }
}