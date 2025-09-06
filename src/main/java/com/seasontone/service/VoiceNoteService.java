package com.seasontone.service;

import com.seasontone.Entity.ChecklistItems;
import com.seasontone.Entity.RecordVoiceNote;
import com.seasontone.Entity.UserRecord;
import com.seasontone.dto.VoiceNoteDto;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.repository.UserRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class VoiceNoteService {

  private final UserRecordRepository recordRepo;
  private final RecordVoiceNoteRepository voiceRepo;
  private final WhisperClient whisper;     // 훅(나중에 실제 구현)
  private final TextSummarizer summarizer; // 훅(나중에 실제 구현)

  // 음성 업로드(교체) → BLOB 저장 + 전사/요약 훅
  @Transactional
  public VoiceNoteDto uploadReplace(Long checkId, Long userId, MultipartFile file, Integer durationSec) throws IOException {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
    if (file.getSize() > 20 * 1024 * 1024) throw new IllegalArgumentException("file too large (<=20MB)");

    UserRecord r = recordRepo.findByIdAndUser_Id(checkId, userId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    if (r.getItems() == null) r.attachBlankItems();
    ChecklistItems items = r.getItems();

    // 기존 음성 있으면 삭제(교체)
    voiceRepo.findByItems_Id(items.getId()).ifPresent(voiceRepo::delete);

    RecordVoiceNote v = new RecordVoiceNote();
    v.setItems(items);
    v.setFilename(file.getOriginalFilename());
    v.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
    v.setSize(file.getSize());
    v.setDurationSec(durationSec);
    v.setData(file.getBytes());

    // Whisper/요약(목업) — 실패해도 저장은 진행
    try {
      String transcript = whisper.transcribeBytes(file.getBytes(), v.getContentType());
      String summary = summarizer.summarize(transcript);
      v.setTranscript(transcript);
      v.setSummary(summary);

      // memo에 요약 반영 (없으면 덮어쓰기)
      String newMemo = (items.getMemo()==null || items.getMemo().isBlank())
          ? summary
          : items.getMemo() + "\n\n[음성 요약]\n" + summary;
      items.setMemo(newMemo);

    } catch (Exception ignore) {}

    RecordVoiceNote saved = voiceRepo.save(v);

    return new VoiceNoteDto(
        saved.getFilename(), saved.getContentType(), saved.getSize(), saved.getDurationSec(),
        saved.getTranscript(), saved.getSummary(), saved.getCreatedAt(), saved.getUpdatedAt(),
        "/api/checklists/%d/audio/raw".formatted(checkId)
    );
  }

  @Transactional(readOnly = true)
  public VoiceNoteDto getMeta(Long checkId){
    UserRecord r = recordRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    if (r.getItems() == null) throw new EntityNotFoundException("Items not found");
    RecordVoiceNote v = voiceRepo.findByItems_Id(r.getItems().getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice note not found"));

    return new VoiceNoteDto(
        v.getFilename(), v.getContentType(), v.getSize(), v.getDurationSec(),
        v.getTranscript(), v.getSummary(), v.getCreatedAt(), v.getUpdatedAt(),
        "/api/checklists/%d/audio/raw".formatted(checkId)
    );
  }

  @Transactional(readOnly = true)
  public RecordVoiceNote loadEntityForRaw(Long checkId){
    UserRecord r = recordRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    if (r.getItems() == null) throw new EntityNotFoundException("Items not found");
    return voiceRepo.findByItems_Id(r.getItems().getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice note not found"));
  }

  @Transactional
  public void delete(Long checkId, Long userId){
    UserRecord r = recordRepo.findByIdAndUser_Id(checkId, userId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    if (r.getItems() == null) return;
    voiceRepo.deleteByItems_Id(r.getItems().getId());
  }
}
