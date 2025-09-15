package com.seasontone.domain.checklists;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "items_id", nullable = false, unique = true)
  private ChecklistItems items;

  @Column(length = 255) private String filename;
  @Column(length = 255) private String contentType;
  private Long size;
  private Integer durationSec;

  @Lob @Basic(fetch = FetchType.LAZY)
  @Column(name = "data", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] data;

  @Lob @Column(columnDefinition = "LONGTEXT") private String transcript;
  @Lob @Column(columnDefinition = "LONGTEXT") private String summary;

  @CreationTimestamp @Column(name = "created_at", updatable = false)
  private Instant createdAt;
  @UpdateTimestamp @Column(name = "updated_at")
  private Instant updatedAt;
}