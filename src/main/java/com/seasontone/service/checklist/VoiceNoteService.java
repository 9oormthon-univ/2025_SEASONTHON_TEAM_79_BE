package com.seasontone.service.checklist;

import com.seasontone.dto.voice.VoiceNoteDto;
import com.seasontone.domain.checklists.ChecklistItems;
import com.seasontone.domain.checklists.RecordVoiceNote;
import com.seasontone.repository.ChecklistItemsRepository;
import com.seasontone.repository.RecordVoiceNoteRepository;
import com.seasontone.service.TextSummarizer;
import com.seasontone.service.WhisperClient;
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
  private final ChecklistItemsRepository itemsRepo;
  private final RecordVoiceNoteRepository voiceRepo;
  private final WhisperClient whisper;
  private final TextSummarizer summarizer;

  @Transactional
  public VoiceNoteDto uploadReplace(Long checkId, Long userId, MultipartFile file, Integer durationSec) throws IOException {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
    if (file.getSize() > 20 * 1024 * 1024) throw new IllegalArgumentException("file too large (<=20MB)");

    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, userId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));

    // 기존 음성 삭제(1:1)
    voiceRepo.findByItems_Id(i.getId()).ifPresent(voiceRepo::delete);

    RecordVoiceNote v = new RecordVoiceNote();
    v.setItems(i);
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
    } catch (Exception ignore) { }

    RecordVoiceNote saved = voiceRepo.save(v);
    return new VoiceNoteDto(
        saved.getFilename(), saved.getContentType(), saved.getSize(), saved.getDurationSec(),
        saved.getTranscript(), saved.getSummary(), saved.getCreatedAt(), saved.getUpdatedAt(),
        "/api/checklists/%d/audio/raw".formatted(checkId)
    );
  }

  @Transactional(readOnly = true)
  public VoiceNoteDto getMeta(Long checkId){
    ChecklistItems i = itemsRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    RecordVoiceNote v = voiceRepo.findByItems_Id(i.getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice note not found"));
    return new VoiceNoteDto(
        v.getFilename(), v.getContentType(), v.getSize(), v.getDurationSec(),
        v.getTranscript(), v.getSummary(), v.getCreatedAt(), v.getUpdatedAt(),
        "/api/checklists/%d/audio/raw".formatted(checkId)
    );
  }

  @Transactional(readOnly = true)
  public RecordVoiceNote loadForRaw(Long checkId){
    ChecklistItems i = itemsRepo.findById(checkId)
        .orElseThrow(() -> new EntityNotFoundException("Checklist not found"));
    return voiceRepo.findByItems_Id(i.getId())
        .orElseThrow(() -> new EntityNotFoundException("Voice note not found"));
  }

  @Transactional
  public void delete(Long checkId, Long userId){
    ChecklistItems i = itemsRepo.findByIdAndUser_Id(checkId, userId)
        .orElseThrow(() -> new AccessDeniedException("Not owner"));
    voiceRepo.deleteByItems_Id(i.getId());
  }
}
