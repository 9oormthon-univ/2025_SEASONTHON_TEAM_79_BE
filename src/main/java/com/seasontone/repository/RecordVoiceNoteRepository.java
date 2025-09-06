package com.seasontone.repository;

import com.seasontone.entity.RecordVoiceNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordVoiceNoteRepository extends JpaRepository<RecordVoiceNote, Long> {
  Optional<RecordVoiceNote> findByItems_Id(Long itemsId);
  long deleteByItems_Id(Long itemsId);
}
