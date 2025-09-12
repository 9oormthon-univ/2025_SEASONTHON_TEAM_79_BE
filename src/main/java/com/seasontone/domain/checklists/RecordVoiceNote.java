package com.seasontone.domain.checklists;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity @Getter @Setter
@Table(name = "record_voice_note")
public class RecordVoiceNote {

  @Id
  @Column(name = "items_id")
  private Long id;                                   // Items PK 공유

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId                                           // ★ PK 공유
  @JoinColumn(name = "items_id")
  private ChecklistItems items;

  @Column(length = 255)
  private String filename;

  @Column(length = 255)
  private String contentType;

  private Long size;             // bytes
  private Integer durationSec;   // (선택) 길이

  @Lob @Basic(fetch = FetchType.LAZY)
  @Column(name = "data", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] data;

  @Lob @Column(columnDefinition = "LONGTEXT")
  private String transcript;     // (추후 Whisper 결과)

  @Lob @Column(columnDefinition = "LONGTEXT")
  private String summary;        // (추후 GPT 요약)

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) createdAt = Instant.now();
    if (updatedAt == null) updatedAt = createdAt;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
