package com.seasontone.service;

import com.seasontone.dto.VoiceNoteDto;
import com.seasontone.entity.ChecklistItems;
import com.seasontone.entity.RecordVoiceNote;
import com.seasontone.entity.UserRecord;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.repository.UserRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class VoiceNoteService {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final UserRecordRepository recordRepo;
  private final RecordVoiceNoteRepository voiceRepo;
   private final WhisperClient whisper;     // ← 이제 실제 구현(OpenAiWhisperClient)이 주입됨
  private final TextSummarizer summarizer; // ← OpenAiTextSummarizer 주입

  @Transactional
  public VoiceNoteDto uploadReplace(Long checkId, Long userId, MultipartFile file, Integer durationSec) throws IOException {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
    if (file.getSize() > 20 * 1024 * 1024) throw new IllegalArgumentException("file too large (<=20MB)");

    UserRecord r = recordRepo.findByIdAndUser_Id(checkId, userId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    if (r.getItems() == null) r.attachBlankItems();
    ChecklistItems items = r.getItems();

    voiceRepo.findByItems_Id(items.getId()).ifPresent(voiceRepo::delete);

    RecordVoiceNote v = new RecordVoiceNote();
    v.setItems(items);
    v.setFilename(file.getOriginalFilename());
    v.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
    v.setSize(file.getSize());
    v.setDurationSec(durationSec);
    v.setData(file.getBytes());

    try {
      String transcript = whisper.transcribeBytes(file.getBytes(), v.getContentType());
      String summary = summarizer.summarize(transcript);
      v.setTranscript(transcript);
      v.setSummary(summary);

      String newMemo = (items.getMemo()==null || items.getMemo().isBlank())
          ? summary
          : items.getMemo() + "\n\n[음성 요약]\n" + summary;
      items.setMemo(newMemo);

    } catch (Exception e) {
      log.error("[VOICE] transcribe/summarize failed", e); // ★ 로그로 원인 확인
    }

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
